package tech.kotlin.common.rpc

import tech.kotlin.common.os.HandlerThread
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


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ServiceContext : HandlerThread("ServiceContext") {

    private var executorService by Delegates.notNull<ExecutorService>()
    private var publishAddress by Delegates.notNull<InetSocketAddress>()
    private var consumer by Delegates.notNull<TcpConsumer>()

    private val provider = Provider()
    private val initLatch = CountDownLatch(1)
    private val services = ConcurrentHashMap<Class<*>, Any>()

    fun init(executorService: ExecutorService, port: Int) {
        this.executorService = executorService
        this.publishAddress = InetSocketAddress("0.0.0.0", port)
        this.start()
        initLatch.await()
    }

    override fun onLooperPrepared() {
        consumer = TcpConsumer()
        initLatch.countDown()//init finish
    }

    fun <T : Any> register(interfaceType: Class<T>, implement: T) {
        provider.register(interfaceType, implement)
        services[interfaceType] = implement
    }

    internal class TcpConsumer : Consumer() {

        var messenger by Delegates.notNull<RpcMessenger>()
        var workThread by Delegates.notNull<IOThread>()

        init {
            this.messenger = RpcMessenger()
            this.workThread = IOThread(messenger)
            this.workThread.start()
            this.workThread.listen(publishAddress)
        }

        override fun onProxyStart(serviceName: String, serviceId: Int, requestId: Long, argument: Any) {
            TODO("not implemented")
        }


        class RpcMessenger : TcpHandler() {

            override fun onConnect(connection: Connection) {

            }

            override fun onData(connection: Connection, data: TcpPackage) {

            }

            override fun onDisconnected(connection: Connection) {

            }

        }
    }

}


