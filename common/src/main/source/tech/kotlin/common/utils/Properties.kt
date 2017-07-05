package tech.kotlin.common.utils

import tech.kotlin.service.Err
import java.io.FileInputStream
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Props : Properties() {
    fun init(file: String = "") {
        if (file.isNullOrBlank()) {
            javaClass.classLoader.getResourceAsStream("default.properties").use { load(it) }
        } else {
            FileInputStream(file).use { load(it) }
        }
    }
}

infix fun Properties.str(field: String): String = getProperty(field) ?: abort(Err.SYSTEM, "no such field $field")

infix fun Properties.long(field: String) = getProperty(field).toLong()

infix fun Properties.int(field: String) = getProperty(field).toInt()

infix fun Properties.bool(field: String) = getProperty(field).toBoolean()