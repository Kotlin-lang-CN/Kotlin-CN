package cn.kotliner.forum.utils.algorithm

import com.baidu.bjf.remoting.protobuf.Codec
import com.baidu.bjf.remoting.protobuf.ProtobufProxy

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ProtoBuf {

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
        return loads(data, T::class.java)
    }

    fun <T : Any> loads(data: ByteArray, type: Class<T>): T {
        val coderMap = coders.get()
        var coder = coderMap[type]
        if (coder == null) {
            coder = ProtobufProxy.create(type)
            coderMap[type] = coder
        }
        @Suppress("UNCHECKED_CAST")
        return (coder!! as Codec<T>).decode(data)
    }
}