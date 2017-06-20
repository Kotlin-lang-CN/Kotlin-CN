package tech.kotlin.common.utils

import tech.kotlin.common.os.Abort
import tech.kotlin.service.Err
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

}

infix fun Properties.str(field: String): String = getProperty(field) ?: abort(Err.SYSTEM, "no such prop field $field")

infix fun Properties.long(field: String) = getProperty(field).toLong()

infix fun Properties.int(field: String) = getProperty(field).toInt()

infix fun Properties.bool(field: String) = getProperty(field).toBoolean()
