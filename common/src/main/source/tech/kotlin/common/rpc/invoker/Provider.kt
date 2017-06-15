package tech.kotlin.common.rpc.invoker

import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.ProtoCode
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.exceptions.ServiceNotFound
import tech.kotlin.common.serialize.Proto
import java.lang.IllegalStateException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Provider {

    private val services = HashMap<Int, InvokeContext>()

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
                            InvokeContext(implement, implement.javaClass.getMethod(method.name, *method.parameterTypes))
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
        val context = services[type] ?:
                return invokeCall(ProtoCode.NOT_FOUND, Proto.dumps(ServiceNotFound("no such interface $type").model))
        try {
            val argType = context.method.parameterTypes[0]
            val result = context.method(context.impl, Proto.loads(data, argType))
            val resultData = Proto.dumps(result)
            invokeCall(type, resultData)
        } catch (err: InvocationTargetException) {
            val targetErr = err.targetException
            if (targetErr is Abort) {
                Log.d(targetErr)
                invokeCall(targetErr.model.code, Proto.dumps(targetErr.model))
            } else {
                Log.e(targetErr)
                invokeCall(ProtoCode.UNKNOWN, Proto.dumps(Abort(ProtoCode.UNKNOWN, targetErr.message).model))
            }
        } catch (err: Throwable) {
            Log.e(err)
            invokeCall(ProtoCode.UNKNOWN, Proto.dumps(Abort(ProtoCode.UNKNOWN, err.message).model))
        }
    }

    internal data class InvokeContext(val impl: Any, val method: Method)
}