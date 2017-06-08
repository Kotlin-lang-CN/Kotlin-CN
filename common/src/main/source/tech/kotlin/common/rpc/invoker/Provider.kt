package tech.kotlin.common.rpc.invoker

import com.baidu.bjf.remoting.protobuf.Codec
import com.baidu.bjf.remoting.protobuf.ProtobufProxy
import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.exceptions.NoSuchServiceError
import tech.kotlin.common.tcp.Connection
import java.lang.IllegalStateException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Provider {

    private val services = HashMap<Int, Pair<Any, Method>>()
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

    fun register(interfaceType: Class<*>, implement: Any) {
        if (interfaceType.declaredMethods
                .filter { it.isAnnotationPresent(RpcInterface::class.java) }
                .map {
                    if (it.parameterTypes.size != 1)
                        throw IllegalStateException("interface method should only have one argument")
                    it
                }
                .map { services[it.getAnnotation(RpcInterface::class.java).value] = implement to it }
                .isEmpty()) {
            throw IllegalStateException("""
            no method is found in interface type ${interfaceType.name},
            rpc service method should be annotated by ${RpcInterface::class.java.name}
            """.trimIndent().trim())
        }
    }

    @Throws(Abort::class)
    fun invokeNative(connection: Connection, service: Int, data: ByteArray): Any {
        val implProxy = services[service] ?: throw NoSuchServiceError("no such service $service")
        try {
            val argType = implProxy.second.parameterTypes[0]
            val arg = (getCodec(argType) as Codec<*>).decode(data)
            val result = implProxy.second(implProxy.first, arg)
            return Any()
        } catch (err: InvocationTargetException) {
            val targetErr = err.targetException
            if (targetErr is Abort) {
                Log.d(err)
                throw targetErr
            } else {
                Log.e(err)
                throw Abort(0, err.message)
            }
        } catch (err: Throwable) {
            Log.e(err)
            throw Abort(0, err.message)
        }
    }

}