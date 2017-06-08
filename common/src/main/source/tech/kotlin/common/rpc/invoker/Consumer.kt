package tech.kotlin.common.rpc.invoker

import com.baidu.bjf.remoting.protobuf.Codec
import com.baidu.bjf.remoting.protobuf.ProtobufProxy
import com.relops.snowflake.Snowflake
import tech.kotlin.common.os.Abort
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.exceptions.ProxyTimeout
import java.lang.reflect.Proxy.newProxyInstance
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
abstract class Consumer {

    private val transactions = ConcurrentHashMap<Long, Transaction>()
    private val coder = ConcurrentHashMap<Class<*>, Codec<*>>()

    private fun <T> getCodec(type: Class<T>): Codec<T> {
        @Suppress("UNCHECKED_CAST")
        return (coder[type] ?: synchronized(coder) {
            val shadow = coder[type]
            if (shadow != null) return@synchronized shadow
            val new = ProtobufProxy.create(type)
            coder[type] = new
            return@synchronized new
        }) as Codec<T>
    }

    fun <T> getProxy(interfaceType: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return newProxyInstance(javaClass.classLoader, arrayOf(interfaceType)) { proxy, method, args ->
            if (!method.isAnnotationPresent(RpcInterface::class.java))
                return@newProxyInstance method.invoke(proxy, *args)

            val requestId = Snowflake(0).next()
            val serviceId = method.getAnnotation(RpcInterface::class.java).value
            val transaction = Transaction()
            synchronized(transaction) {
                try {
                    transactions[requestId] = transaction
                    val data = (getCodec(method.parameterTypes[0]) as Codec<Any>).encode(args[0])
                    onProxyTransport(requestId, serviceId, data)
                } catch (error: Throwable) {
                    transactions.remove(requestId)
                    throw error
                }
            }
            transaction.latch.await(5, TimeUnit.SECONDS)
            val error = transaction.error
            val result = transaction.result
            if (error == null && result == null) {
                throw ProxyTimeout("proxy timeout while invoke $serviceId(${interfaceType.name}$${method.name})")
            } else if (error != null) {
                error.stackTrace = Thread.currentThread().stackTrace
                        .dropWhile { it.methodName != method.name }.toTypedArray()
                throw error
            } else {
                return@newProxyInstance result
            }
        } as T
    }

    abstract fun onProxyTransport(requestId: Long, type: Int, data: ByteArray)

    fun onProxyResult(requestId: Long, type: Int, data: ByteArray) {
        val trans = transactions.remove(requestId) ?: return
        synchronized(trans) {

            trans.latch.countDown()
        }
    }

    data class Transaction(val latch: CountDownLatch = CountDownLatch(1),
                           var result: Any? = null, var error: Abort? = null)
}