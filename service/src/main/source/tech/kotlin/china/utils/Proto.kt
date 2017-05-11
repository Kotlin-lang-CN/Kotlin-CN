package tech.kotlin.china.utils

import com.baidu.bjf.remoting.protobuf.Codec
import com.baidu.bjf.remoting.protobuf.ProtobufProxy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Proto {

    @JvmField
    val coders = object : ThreadLocal<HashMap<Class<*>, Codec<*>>>() {
        override fun get(): HashMap<Class<*>, Codec<*>> {
            val result = super.get()
            if (result == null) {
                val newInstance = HashMap<Class<*>, Codec<*>>()
                set(newInstance)
                return newInstance
            } else {
                return result
            }
        }
    }

    fun <T : Any> dumps(any: T): ByteArray {
        val coderMap = coders.get()
        var coder = coderMap[any.javaClass]
        if (coder == null) {
            coder = ProtobufProxy.create(any.javaClass)
            coderMap[any.javaClass] = coder
        }
        @Suppress("UNCHECKED_CAST")
        return (coder!! as Codec<T>).encode(any)
    }

    inline fun <reified T : Any> loads(data: ByteArray): T {
        val coderMap = coders.get()
        var coder = coderMap[T::class.java]
        if (coder == null) {
            coder = ProtobufProxy.create(T::class.java)
            coderMap[T::class.java] = coder
        }
        @Suppress("UNCHECKED_CAST")
        return (coder!! as Codec<T>).decode(data)
    }
}