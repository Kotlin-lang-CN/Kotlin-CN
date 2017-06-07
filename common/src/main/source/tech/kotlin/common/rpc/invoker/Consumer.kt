package tech.kotlin.common.rpc.invoker

import com.relops.snowflake.Snowflake
import tech.kotlin.common.os.Abort
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.annotations.RpcName
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

    fun <T> getProxy(interfaceType: Class<T>): T {
        val namePrefix = interfaceType.getAnnotation(RpcName::class.java)?.value
                ?: throw IllegalStateException("""
                interface type ${interfaceType.name} should be annotated by ${RpcName::class.java.name}
                """.trimIndent().trim())
        @Suppress("UNCHECKED_CAST")
        return newProxyInstance(javaClass.classLoader, arrayOf(interfaceType)) { proxy, method, args ->
            if (!method.isAnnotationPresent(RpcInterface::class.java))
                return@newProxyInstance method.invoke(proxy, *args)

            val requestId = Snowflake(0).next()
            val serviceId = method.getAnnotation(RpcInterface::class.java).value
            val transaction = Transaction()
            synchronized(transaction) {
                transactions[requestId] = transaction
                onProxyStart(namePrefix, serviceId, requestId, args[0])
            }
            transaction.latch.await(5, TimeUnit.SECONDS)
            val error = transaction.error
            val result = transaction.result
            if (error == null && result == null) {
                throw ProxyTimeout("proxy timeout while invoke $namePrefix:$serviceId(${interfaceType.name}$${method.name})")
            } else if (error != null) {
                error.stackTrace = Thread.currentThread().stackTrace
                        .dropWhile { it.methodName != method.name }.toTypedArray()
                throw error
            } else {
                return@newProxyInstance result
            }
        } as T
    }

    abstract fun onProxyStart(serviceName: String, serviceId: Int, requestId: Long, argument: Any)

    fun onProxyResult(requestId: Long, result: Any) {
        val trans = transactions.remove(requestId) ?: return
        synchronized(trans) {
            if (result is Abort) {
                trans.error = result
            } else if (result is Throwable) {
                trans.error = Abort(-3, result.message)
            } else {
                trans.result = result
            }
            trans.latch.countDown()
        }
    }

    data class Transaction(val latch: CountDownLatch = CountDownLatch(1),
                           var result: Any? = null, var error: Abort? = null)
}