package tech.kotlin.common.utils

import com.fasterxml.jackson.core.type.TypeReference
import tech.kotlin.common.serialize.Json
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 读取项目资源文件中的配置文键
 *********************************************************************/
fun Any.prop(vararg files: String): Properties {
    val result = Properties()
    for (file in files) javaClass.classLoader.getResourceAsStream(file.trimStart('/')).use { result.load(it) }
    return result
}

inline fun <reified T : Any> Any.json(file: String): T {
    BufferedReader(InputStreamReader(javaClass.classLoader.getResourceAsStream(file.trimStart('/')))).use { input ->
        val sb = StringBuilder()
        var result: T
        ByteArrayOutputStream().use {
            do {
                val line = input.readLine()
                if (line == null) {
                    result = Json.loads<T>(sb.toString())
                    break
                } else {
                    sb.append(line)
                }
            } while (true)
            return result
        }
    }
}

fun <T : Map<*, *>> Any.map(file: String, type: TypeReference<T>): T {
    BufferedReader(InputStreamReader(javaClass.classLoader.getResourceAsStream(file.trimStart('/')))).use { input ->
        val sb = StringBuilder()
        var result: T
        ByteArrayOutputStream().use {
            do {
                val line = input.readLine()
                if (line == null) {
                    result = Json.loads(sb.toString(), type)
                    break
                } else {
                    sb.append(line)
                }
            } while (true)
            return result
        }
    }
}

