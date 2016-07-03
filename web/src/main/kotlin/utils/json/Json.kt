package utils.json

import com.alibaba.fastjson.JSON
import kotlin.reflect.KClass

fun Any.toJson(): String = JSON.toJSONString(this)

fun Any.toJsonBytes(): ByteArray = JSON.toJSONBytes(this)

fun <T : Any> String.toModel(type: KClass<T>): T = JSON.parseObject<T>(this, type.java)

fun <T : Any> ByteArray.toModel(type: KClass<T>): T = JSON.parseObject<T>(this, type.java)