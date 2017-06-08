package tech.kotlin.common.rpc

import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.rpc.exceptions.ServiceErr
import tech.kotlin.common.rpc.invoker.Consumer
import tech.kotlin.common.rpc.invoker.Provider
import tech.kotlin.common.tcp.Connection
import tech.kotlin.common.tcp.IOThread
import tech.kotlin.common.tcp.TcpHandler
import tech.kotlin.common.tcp.TcpPackage
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Service : HandlerThread("Service") {

    private var executorService by Delegates.notNull<ExecutorService>()
    private var ioThread by Delegates.notNull<IOThread>()
    private var port by Delegates.notNull<Int>()

    private val provider = Provider()
    private val initLatch = CountDownLatch(1)
    private val services = ConcurrentHashMap<Class<*>, Any>()

    fun init() {
        start()
        initLatch.await()
    }

    override fun onLooperPrepared() {
        ioThread = IOThread(RpcHandler())
        ioThread.start()
        initLatch.countDown()//init finish
    }

    fun <T : Any> register(interfaceType: Class<T>, implement: T) {
        provider.register(interfaceType, implement)
        services[interfaceType] = implement
    }

    fun publish(executorService: ExecutorService, port: Int) {
        this.executorService = executorService
        this.port = port
        ioThread.listen(InetSocketAddress("0.0.0.0", port))
    }

    class RpcHandler : TcpHandler() {

        override fun onConnect(connection: Connection) {

        }

        override fun onData(connection: Connection, data: TcpPackage) {
            val binder = connection.attachment()
            if (binder == null) {
                executorService.submit {
                    try {
                        //provider.invokeNative(data.type, data.data)
                    } catch (abort: Abort) {

                    } catch (err: Throwable) {

                    }
                }
            } else {
                (binder as bind<*>).onProxyResult(data.packageId, data.type, data.data) //proxy invoke callback
            }
        }

        override fun onDisconnected(connection: Connection) {
            connection.attachment()?.apply { (this as bind<*>).conn = null; this.generateService() }
        }

    }

    class bind<T : Any>(val api: KClass<T>) : ReadOnlyProperty<Any, T>, Consumer() {

        var conn: Connection? = null

        fun generateService(): T {
            val proxy = getProxy(api.java)
            conn = ioThread.connect(InetSocketAddress("127.0.0.1", port)).apply { attach(this) }
            return proxy
        }

        override fun onProxyTransport(requestId: Long, type: Int, data: ByteArray) {
            val connection = conn ?: throw ServiceErr("service is not ready")
            if (connection.send(type, requestId, data) == 0) {
                throw ServiceErr("service busy while invoke $type")
            }
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val localImpl = services[api.java]
            @Suppress("UNCHECKED_CAST")
            if (localImpl != null) return localImpl as T
            return generateService()
        }

    }
}


