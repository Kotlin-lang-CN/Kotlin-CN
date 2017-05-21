package tech.kotlin.common.rpc;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import kotlin.jvm.functions.Function2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.kotlin.common.exceptions.Abort;
import tech.kotlin.common.os.HandlerThread;
import tech.kotlin.common.os.Looper;
import tech.kotlin.common.tcp.Connection;
import tech.kotlin.common.tcp.IOThread;
import tech.kotlin.common.tcp.TcpHandler;
import tech.kotlin.common.tcp.TcpPackage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class ServiceContext extends HandlerThread {

    private static final Logger log = LogManager.getLogger(ServiceContext.class.getSimpleName());
    private static final int RPC_FAIL = -1;

    private IOThread tcpThread;
    private RPCMessenger messenger;

    private final ExecutorService service;
    private final String name;
    private final CountDownLatch initLatch;

    private final Map<Class, Object> instanceMap = new HashMap<>();
    private final Map<Integer, Method> rpcMap = new HashMap<>();
    private final Codec<Abort.ErrorModel> errorCoder = ProtobufProxy.create(Abort.ErrorModel.class);

    public <API> API get(Class<API> service) {
        return get(service, 5/*s*/);
    }

    public <API> API get(Class<API> service, long invokeTimeout/*ms*/) {
        //noinspection unchecked
        return (API) (instanceMap.containsKey(service) ?
                /*local implements*/instanceMap.get(service) :
                /*remote implements*/messenger.get(service, invokeTimeout));
    }

    public <API, IMPLEMENT extends API> void register(Class<API> api, IMPLEMENT impl) {
        internalRegister(api, impl);
    }

    public void connect(InetSocketAddress address, int connCount) throws IOException {
        for (int i = 0; i < connCount; i++) messenger.connect(address);
    }

    public void listen(InetSocketAddress address) throws IOException {
        tcpThread.listen(address);
    }

    void internalRegister(Class api, Object impl) {
        Stream.of(api.getMethods()
        ).filter(method -> method.isAnnotationPresent(RpcInterface.class)
        ).forEach(method -> {
            int rpcKey = method.getAnnotation(RpcInterface.class).value();
            if (rpcMap.containsKey(rpcKey)) {
                throw new IllegalStateException(String.format("%s rpc key %d conflict, %s and %s",
                        name, rpcKey, rpcMap.get(rpcKey).getName(), api.getName()));
            }
            rpcMap.put(rpcKey, method);
            log.info(String.format(name + " register rpc method {%s#%s, type=%d}",
                    api.getName(), method.getName(), rpcKey));
        });
        instanceMap.put(api, impl);
    }

    ServiceContext(String name, ExecutorService service, CountDownLatch latch) {
        super("rpc:" + name);
        this.name = "rpc:" + name;
        this.service = service;
        this.initLatch = latch;
    }

    @Override
    protected void onLooperPrepared() {
        try {
            messenger = new RPCMessenger(Looper.myLooper());
            tcpThread = new IOThread(messenger);
            tcpThread.start();
            log.info("-" + name + " resource start-");
        } catch (IOException e) {
            log.error(e);
            throw new IllegalStateException(e);
        } finally {
            initLatch.countDown();
        }
    }

    @Override
    protected void onThreadExit() {
        messenger.close();
    }

    private final class RPCMessenger extends TcpHandler {

        private final List<Connection> connPool = new ArrayList<>();

        RPCMessenger(Looper looper) {
            super(looper);
        }

        @Override
        public void onConnect(Connection connection) {
            connPool.add(connection);
        }

        @Override
        public void onDisconnected(Connection connection) {
            connPool.remove(connection);
        }

        @Override
        public void onData(Connection connection, TcpPackage data) {
            ResultCall call = (ResultCall) connection.attachment();
            if (call != null) {
                try {
                    invokeCallback(data, call);
                } finally {
                    connection.attach(null);
                    connPool.add(connection);
                }
            } else {
                connPool.remove(connection);
                nativeInvoke(data, (code, resp) -> {
                    connection.send(code, resp);
                    connPool.add(connection);
                    return null;
                });
            }
        }

        <Req, Resp> void nativeInvoke(TcpPackage data, Function2<Integer, byte[], Void> onInvoke) {
            Method api = rpcMap.get(data.type);
            if (api == null) {
                log.error(name + " no such rpc service with type " + data.type);
                return;
            }
            Object impl = instanceMap.get(api.getDeclaringClass());
            if (service == null)
                return;

            service.submit(() -> {
                @SuppressWarnings("unchecked")
                Codec<Req> reqCodec = ProtobufProxy.create((Class<Req>) api.getParameterTypes()[0]);
                @SuppressWarnings("unchecked")
                Codec<Resp> respCodec = ProtobufProxy.create((Class<Resp>) api.getReturnType());
                try {
                    @SuppressWarnings("unchecked")
                    Resp obj = (Resp) api.invoke(impl, reqCodec.decode(data.data));//may throw exception here
                    byte[] response = respCodec.encode(obj);
                    post(() -> onInvoke.invoke(data.type, response));
                } catch (InvocationTargetException e) {
                    Throwable error = e.getTargetException();
                    byte[] bytes;
                    try {
                        if (error instanceof Abort) {
                            bytes = errorCoder.encode(((Abort) error).getModel());
                        } else {
                            bytes = errorCoder.encode(new Abort.ErrorModel());
                            log.error(error);
                        }
                        post(() -> onInvoke.invoke(RPC_FAIL, bytes));
                    } catch (IOException ignore) {
                        log.error(ignore);
                    }
                } catch (Exception e) {
                    try {
                        byte[] bytes = errorCoder.encode(new Abort.ErrorModel());
                        post(() -> onInvoke.invoke(RPC_FAIL, bytes));
                    } catch (IOException ignore) {
                        log.error(ignore);
                    }
                }
            });
        }

        void invokeCallback(TcpPackage data, ResultCall call) {
            try {
                if (data.type == RPC_FAIL) {
                    Abort.ErrorModel model = errorCoder.decode(data.data);
                    call.error(new Abort(model.code, model.message));
                } else {
                    call.result(data.data);
                }
            } catch (Throwable error) {
                throw new Abort(0, "decode remote package error!");
            }
        }

        Object get(Class service, long invokeTimeout) {
            return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, (proxy, method, args) -> {
                RpcInterface rpc = method.getAnnotation(RpcInterface.class);
                if (rpc == null) return method.invoke(proxy, args);

                final CountDownLatch latch = new CountDownLatch(1);
                ResultCall call = new ResultCall(ProtobufProxy.create(method.getReturnType()), latch);
                @SuppressWarnings("unchecked")
                byte[] request = ((Codec) ProtobufProxy.create(method.getParameterTypes()[0])).encode(args[0]);
                post(() -> {
                    if (connPool.isEmpty()) {
                        call.error(new Abort(0, "rpc busy"));
                    } else {
                        Connection connection = connPool.remove(0);
                        connection.attach(call);
                        connection.send(rpc.value(), request);
                    }
                });

                latch.await(invokeTimeout, TimeUnit.SECONDS);
                return call.getResult();
            });
        }
    }

    private class ResultCall {

        private final CountDownLatch latch;
        private final Codec respCoder;

        private volatile boolean hasResult;
        private volatile Object result;
        private volatile Abort error;

        ResultCall(Codec respCoder, CountDownLatch latch) {
            this.respCoder = respCoder;
            this.latch = latch;
        }

        void result(byte[] result) throws Abort {
            synchronized (this) {
                try {
                    this.result = respCoder.decode(result);
                    this.error = null;
                    this.hasResult = true;
                } catch (IOException e) {
                    throw new Abort(0, "decode remote package error!");
                } finally {
                    latch.countDown();
                }
            }
        }

        void error(Abort error) {
            synchronized (this) {
                try {
                    this.result = null;
                    this.error = error;
                    this.hasResult = true;
                } finally {
                    latch.countDown();
                }
            }
        }

        Object getResult() throws Abort, TimeoutException {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            StackTraceElement[] invoker = new StackTraceElement[elements.length - 4];
            System.arraycopy(elements, 4, invoker, 0, invoker.length);
            if (error != null) {
                error.setStackTrace(invoker);
            }
            synchronized (this) {
                if (!hasResult) {
                    TimeoutException error = new TimeoutException(name + " rpc timeout");
                    error.setStackTrace(invoker);
                    throw error;
                }
                if (error != null)
                    throw error;
                return result;
            }
        }
    }

}
