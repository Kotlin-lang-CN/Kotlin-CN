package tech.kotlin.common.utils

import com.relops.snowflake.Snowflake
import java.lang.management.ManagementFactory

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
internal val pid by lazy { ManagementFactory.getRuntimeMXBean().name.split("@")[0].toInt() }

object IDs : Snowflake(pid % 1024) {

    val symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-".toCharArray()

    fun decode(s: String): Long {
        val B = symbols.size
        var num: Long = 0
        for (ch in s.toCharArray()) {
            num *= B.toLong()
            num += symbols.indexOfFirst { it == ch }.toLong()
        }
        return num
    }

    fun encode(num: Long): String {
        var tmp = num
        val B = symbols.size
        val sb = StringBuilder()
        while (tmp != 0L) {
            sb.append(symbols[(tmp % B).toInt()])
            tmp /= B.toLong()
        }
        return sb.reverse().toString()
    }
}

fun main(args: Array<String>) {
    val id = IDs.next()
    println(id)
    println(IDs.encode(id))
    println(IDs.decode(IDs.encode(id)))
}