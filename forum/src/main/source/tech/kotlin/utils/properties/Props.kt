package tech.kotlin.utils.properties

import com.fasterxml.jackson.core.type.TypeReference
import tech.kotlin.utils.serialize.Json
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
object Props {

    fun loads(vararg files: String): Properties {
        val result = Properties()
        for (file in files) javaClass.classLoader.getResourceAsStream(file.trimStart('/')).use { result.load(it) }
        return result
    }

    inline fun <reified T : Any> loadJson(file: String): T {
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

    fun <T : Any> loadJson(file: String, type: TypeReference<T>): T {
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

}

infix fun Properties.str(field: String): String = getProperty(field)

infix fun Properties.long(field: String) = getProperty(field).toLong()

infix fun Properties.int(field: String) = getProperty(field).toInt()
