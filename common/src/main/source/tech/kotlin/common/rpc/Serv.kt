package tech.kotlin.common.rpc

import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.exceptions.ServiceErr
import tech.kotlin.common.rpc.invoker.Consumer
import tech.kotlin.common.rpc.invoker.Provider
import tech.kotlin.common.tcp.Connection
import tech.kotlin.common.tcp.IOThread
import tech.kotlin.common.tcp.TcpHandler
import tech.kotlin.common.tcp.TcpPackage
import java.net.InetSocketAddress
import java.nio.channels.Selector
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
object Serv : HandlerThread("Serv") {

    private var executorService by Delegates.notNull<ExecutorService>()
    private var ioThread by Delegates.notNull<IOThread>()
    private var port = 8900

    private val init: Unit by lazy { start(); initLatch.await(); Log.d("init finish"); Unit }//延迟加载初始化
    private val provider = Provider()
    private val initLatch = CountDownLatch(1)
    private val services = ConcurrentHashMap<Class<*>, Any>()

    override fun onLooperPrepared() {
        ioThread = IOThread(RpcHandler())
        ioThread.start()
        initLatch.countDown()//init finish
    }

    fun <T : Any> register(interfaceType: Class<T>, implement: T) {
        init
        provider.register(interfaceType, implement)
        services[interfaceType] = implement
    }

    fun publish(executorService: ExecutorService, port: Int, name: String) {
        init
        this.executorService = executorService
        this.port = port
        ioThread.listen(InetSocketAddress("0.0.0.0", port))
        //TODO: publish service to etcd.
    }

    internal class RpcHandler : TcpHandler() {

        override fun onConnect(connection: Connection) {
            Log.i("new connection init")
        }

        override fun onData(connection: Connection, data: TcpPackage) {
            val binder = connection.attachment()
            if (binder == null) {
                executorService.submit {
                    provider.invokeNative(data.type, data.data, { type, resp ->
                        val result = connection.send(type, data.packageId, resp)
                        if (result != resp.size + 16) Log.e("resp size is ${resp.size + 16}, but result size $result")
                    })
                }
            } else {
                (binder as bind<*>).onProxyResult(data.packageId, data.type, data.data) //proxy invoke callback
            }
        }

        override fun onDisconnected(connection: Connection) {
            connection.attachment()?.apply { (this as bind<*>).conn = null }
        }

    }

    class bind<T : Any>(val name: String, val api: KClass<T>) : ReadOnlyProperty<Any, T>, Consumer() {

        internal var conn: Connection? = null

        override fun onProxyTransport(requestId: Long, type: Int, data: ByteArray) {
            init
            val connection: Connection = synchronized(this) {
                val connection = conn
                if (connection != null) return@synchronized connection

                //TODO: fetch service from etcd.
                val newConn = ioThread.connect(InetSocketAddress("127.0.0.1", port)).apply { attach(this@bind) }
                conn = newConn
                return@synchronized newConn
            }

            if (connection.send(type, requestId, data) == 0) {
                connection.close()
                conn = null
                throw ServiceErr("service busy while invoke $type")
            }
        }


        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            @Suppress("UNCHECKED_CAST")
            return (services[api.java] ?: getProxy(api.java)) as T
        }
    }
}


