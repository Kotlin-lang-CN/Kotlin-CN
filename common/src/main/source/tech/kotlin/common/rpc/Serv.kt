package tech.kotlin.common.rpc

import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.exceptions.ServiceBusy
import tech.kotlin.common.rpc.invoker.Consumer
import tech.kotlin.common.rpc.invoker.Provider
import tech.kotlin.common.rpc.registrator.ServiceRegistrator
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
object Serv : HandlerThread("Serv") {

    private var registrator by Delegates.notNull<ServiceRegistrator>()
    private var executorService by Delegates.notNull<ExecutorService>()
    private var ioThread by Delegates.notNull<IOThread>()

    private val provider = Provider()
    private val initLatch = CountDownLatch(1)
    private val services = ConcurrentHashMap<Class<*>, Any>()

    fun init(registrator: ServiceRegistrator = object : ServiceRegistrator {
        override fun getService(serviceName: String): InetSocketAddress = TODO("not implments")
        override fun publishService(serviceName: String, address: InetSocketAddress) = Unit
    }) {
        this.registrator = registrator
        start()
        initLatch.await()
        Log.d("service initiate success!")
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

    fun publish(address: InetSocketAddress, serviceName: String, executorService: ExecutorService) {
        this.executorService = executorService
        this.registrator.publishService(serviceName, address)
        this.ioThread.listen(address)
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
            val connection: Connection = synchronized(this) {
                val connection = conn
                if (connection != null)
                    return@synchronized connection

                val address = registrator.getService(name)
                val newConn = ioThread.connect(address).apply { attach(this@bind) }
                conn = newConn
                return@synchronized newConn
            }

            if (connection.send(type, requestId, data) == 0) {
                connection.close()
                conn = null
                throw ServiceBusy("service busy while invoke $type")
            }
        }


        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            @Suppress("UNCHECKED_CAST")
            return (services[api.java] ?: getProxy(api.java)) as T
        }
    }
}


