package tech.kotlin.common.rpc

import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.exceptions.ServiceBusy
import tech.kotlin.common.rpc.invoker.Consumer
import tech.kotlin.common.rpc.invoker.Provider
import tech.kotlin.common.rpc.registrator.ServiceRegistrator
import tech.kotlin.common.serialize.Proto
import tech.kotlin.common.tcp.Connection
import tech.kotlin.common.tcp.IOThread
import tech.kotlin.common.tcp.TcpHandler
import tech.kotlin.common.tcp.TcpPackage
import tech.kotlin.service.domain.EmptyResp
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import java.net.NetworkInterface


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Serv : HandlerThread("Serv") {

    const val TTL = 3000L

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

    fun <T : Any> register(interfaceType: KClass<T>, implement: T) {
        provider.register(interfaceType.java, implement)
        services[interfaceType.java] = implement
    }

    fun publish(broadcastIp: String, port: Int, serviceName: String, executorService: ExecutorService) {
        val serviceIp = NetworkInterface.getNetworkInterfaces().toList().first {
            it.interfaceAddresses.toList().find {
                it?.broadcast?.hostAddress == broadcastIp
            } != null
        }.inetAddresses.toList().first() { it is Inet4Address }.hostName
        Log.i("publish service $serviceName @ $serviceIp:$port")

        this.executorService = executorService
        this.registrator.publishService(serviceName, InetSocketAddress(serviceIp, port))
        this.ioThread.listen(InetSocketAddress(serviceIp, port))
    }

    internal class RpcHandler : TcpHandler() {

        override fun onConnect(connection: Connection) {
            val binder = connection.attachment() ?: return
            (binder as bind<*>).pingSchedule(connection, this)
        }

        override fun onData(connection: Connection, data: TcpPackage) {
            val binder = connection.attachment()
            if (binder == null) {
                if (data.type == ProtoCode.PING) {
                    connection.send(ProtoCode.PONG, data.packageId, Proto.dumps(EmptyResp()))
                } else {
                    executorService.submit {
                        provider.invokeNative(data.type, data.data, { type, resp ->
                            val result = connection.send(type, data.packageId, resp)
                            if (result != resp.size + 16)
                                Log.e("resp size is ${resp.size + 16}, but result size $result")
                        })
                    }
                }
            } else {
                binder as bind<*>
                if (data.type == ProtoCode.PONG) {
                    binder.pingSchedule(connection, this)
                } else {
                    binder.onProxyResult(data.packageId, data.type, data.data) //proxy invoke callback
                }
            }
        }

        override fun onDisconnected(connection: Connection) {
            connection.attachment()?.apply { (this as bind<*>).conn = null }
        }

    }

    class bind<T : Any>(val api: KClass<T>, val name: String = "") : ReadOnlyProperty<Any, T>, Consumer() {

        internal var conn: Connection? = null
        internal var sendSchedule: Runnable? = null
        internal var timeoutSchedule: Runnable? = null
        internal var remoteAddress: InetSocketAddress? = null

        override fun onProxyTransport(requestId: Long, type: Int, data: ByteArray) {
            val connection: Connection = synchronized(this) {
                val connection = conn
                if (connection != null) return@synchronized connection
                Log.i("Service init binder ${api.java.name} for ${this.javaClass.name}")
                val address = registrator.getService(name).apply { remoteAddress = this }
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


        fun pingSchedule(connection: Connection, handler: Handler) {
            synchronized(this) {
                /*cancel current schedule*/
                sendSchedule?.apply { handler.cancel(this) }
                timeoutSchedule?.apply { handler.cancel(this) }
                /*define new schedule*/
                sendSchedule = Runnable {
                    connection.send(ProtoCode.PONG, UUID.randomUUID().mostSignificantBits, Proto.dumps(EmptyResp()))
                }
                timeoutSchedule = Runnable {
                    Log.e("connection to $name-${api.simpleName} @ " +
                            "${remoteAddress?.hostName}:${remoteAddress?.port} disconnected")
                    connection.close()
                }
                /*schedule*/
                handler.postDelay(sendSchedule, TTL)
                handler.postDelay(timeoutSchedule, TTL * 2)
            }
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            @Suppress("UNCHECKED_CAST")
            return (services[api.java] ?: getProxy(api.java)) as T
        }
    }
}


