package utils.json

import com.alibaba.fastjson.JSON

fun Any.toJson(): String = JSON.toJSONString(this)

fun Any.toJsonBytes(): ByteArray = JSON.toJSONBytes(this)

fun <T> String.toModel(type: Class<T>): T = JSON.parseObject<T>(this, type)

fun <T> ByteArray.toModel(type: Class<T>): T = JSON.parseObject<T>(this, type)