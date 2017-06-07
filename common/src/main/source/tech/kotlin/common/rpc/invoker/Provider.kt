package tech.kotlin.common.rpc.invoker

import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.annotations.RpcName
import tech.kotlin.common.rpc.exceptions.NoSuchServiceError
import java.lang.IllegalStateException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Provider {

    private val services = HashMap<String, Pair<Any, Method>>()

    fun register(interfaceType: Class<*>, implement: Any) {
        val namePrefix = interfaceType.getAnnotation(RpcName::class.java)?.value
                ?: throw IllegalStateException("""
                interface type ${interfaceType.name} should be annotated by ${RpcName::class.java.name}
                """.trimIndent().trim())
        if (interfaceType.declaredMethods
                .filter { it.isAnnotationPresent(RpcInterface::class.java) }
                .map {
                    if (it.parameterTypes.size != 1)
                        throw IllegalStateException("interface method should only have one argument")
                    it
                }
                .map { services["$namePrefix:${it.getAnnotation(RpcInterface::class.java).value}"] = implement to it }
                .isEmpty()) {
            throw IllegalStateException("""
            no method is found in interface type ${interfaceType.name},
            rpc service method should be annotated by ${RpcInterface::class.java.name}
            """.trimIndent().trim())
        }
    }

    @Throws(Abort::class)
    fun invokeNative(serviceName: String, arg: Any): Any {
        val implProxy = services[serviceName] ?: throw NoSuchServiceError("no such service with name $serviceName")
        try {
            return implProxy.second(implProxy.first, arg)
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