package tech.kotlin.common.rpc.invoker

import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.exceptions.NoSuchServiceError
import tech.kotlin.common.serialize.Proto
import java.lang.IllegalStateException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Provider {

    private val services = HashMap<Int, InvokeWrapper>()

    fun register(interfaceType: Class<*>, implement: Any) {
        if (interfaceType.declaredMethods
                .filter { it.isAnnotationPresent(RpcInterface::class.java) }
                .map {
                    if (it.parameterTypes.size != 1)
                        throw IllegalStateException("interface method should only have one argument")
                    it
                }
                .map { method ->
                    services[method.getAnnotation(RpcInterface::class.java).value] =
                            InvokeWrapper(implement, implement.javaClass.getMethod(method.name, *method.parameterTypes))
                }
                .isEmpty()) {
            throw IllegalStateException("""
            no method is found in interface type ${interfaceType.name},
            rpc service method should be annotated by ${RpcInterface::class.java.name}
            """.trimIndent().trim())
        }
    }

    @Throws(Abort::class)
    fun invokeNative(type: Int, data: ByteArray, invokeCall: (Int, ByteArray) -> Unit) {
        val wrapper = services[type] ?: return invokeCall(0, Proto.dumps(Abort(0, "no such interface $type").model))
        try {
            val argType = wrapper.method.parameterTypes[0]
            val arg = Proto.loads(data, argType)
            val proxy = wrapper.impl
            val result = wrapper.method(proxy, arg)
            val resultData = Proto.dumps(result)
            invokeCall(type, resultData)
        } catch (err: InvocationTargetException) {
            val targetErr = err.targetException
            if (targetErr is Abort) {
                Log.d(targetErr)
                invokeCall(targetErr.model.code, Proto.dumps(targetErr.model))
            } else {
                Log.e(targetErr)
                invokeCall(0, Proto.dumps(Abort(0, targetErr.message).model))
            }
        } catch (err: Throwable) {
            Log.e(err)
            invokeCall(0, Proto.dumps(Abort(0, err.message).model))
        }
    }

    internal data class InvokeWrapper(val impl: Any, val method: Method)
}