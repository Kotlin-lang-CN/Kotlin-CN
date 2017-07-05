package tech.kotlin.common.os

import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Log {

    const val DISABLED = 0
    const val VERBOSE = 0x1
    const val DEBUG = 0x1 shl 1
    const val INFO = 0x1 shl 2
    const val WARN = 0x1 shl 3
    const val ERROR = 0x1 shl 4

    var LOG_LEVEL = VERBOSE or DEBUG or INFO or WARN or ERROR
    private val pid by lazy { ManagementFactory.getRuntimeMXBean().name.split("@")[0].toInt() }

    //时间格式
    private val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA)
    private val tag: String
        get() {
            val cause = Thread.currentThread().stackTrace
            var tag = Log::class.java.simpleName
            for (i in 1..cause.size - 1) {
                if (Log::class.java.name == cause[i - 1].className && Log::class.java.name != cause[i].className) {
                    tag = cause[i].className.replace("^.*\\.".toRegex(), "")
                    break
                }
            }
            return tag
        }

    //verbose level
    @JvmStatic fun v(tag: String?, message: String?): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message)
    }

    @JvmStatic fun v(tag: String?, message: Long): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message.toString())
    }

    @JvmStatic fun v(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, getDetail(e))
    }

    @JvmStatic fun v(message: String?): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message)
    }

    @JvmStatic fun v(message: Long): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message.toString())
    }

    @JvmStatic fun v(e: Throwable): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, getDetail(e))
    }

    //debug level
    @JvmStatic fun d(tag: String?, message: String?): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message)
    }

    @JvmStatic fun d(tag: String?, message: Long): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message.toString())
    }

    @JvmStatic fun d(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, getDetail(e))
    }

    @JvmStatic fun d(message: String?): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message)
    }

    @JvmStatic fun d(message: Long): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message.toString())
    }

    @JvmStatic fun d(e: Throwable): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, getDetail(e))
    }

    //info level
    @JvmStatic fun i(tag: String?, message: String?): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message)
    }

    @JvmStatic fun i(tag: String?, message: Long): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message.toString())
    }

    @JvmStatic fun i(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, getDetail(e))
    }

    @JvmStatic fun i(message: String?): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message)
    }

    @JvmStatic fun i(message: Long): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message.toString())
    }

    @JvmStatic fun i(e: Throwable): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, getDetail(e))
    }

    //warn level
    @JvmStatic fun w(tag: String?, message: String?): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message)
    }

    @JvmStatic fun w(tag: String?, message: Long): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message.toString())
    }

    @JvmStatic fun w(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, getDetail(e))
    }

    @JvmStatic fun w(message: String?): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message)
    }

    @JvmStatic fun w(message: Long): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message.toString())
    }

    @JvmStatic fun w(e: Throwable): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, getDetail(e))
    }

    //error level
    @JvmStatic fun e(tag: String?, message: String?): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message)
    }

    @JvmStatic fun e(tag: String?, message: Long): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message.toString())
    }

    @JvmStatic fun e(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, getDetail(e))
    }

    @JvmStatic fun e(message: String?): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message)
    }

    @JvmStatic fun e(message: Long): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message.toString())
    }

    @JvmStatic fun e(e: Throwable): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, getDetail(e))
    }

    //private methods
    private fun isLevelLogging(level: Int) = level != DISABLED && level and LOG_LEVEL == level

    private fun getDetail(e: Throwable?): String? {
        if (e == null) return null
        return StringBuilder(e.toString()).apply {
            e.stackTrace.forEach { append(it).append("\n") }
        }.toString()
    }

    private fun log(level: Int, tag: String?, message: String?): Boolean {
        LoggerImpl.log(level, tag, message)
        return true
    }

    private object LoggerImpl {

        fun getLevel(level: Int) = when (level) {
            VERBOSE -> "V"
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
            else -> "NULL"
        }

        fun log(level: Int, tag: String?, line: String?) {
            if (tag.isNullOrBlank() || line.isNullOrBlank()) return
            System.out.printf("%s %s/%s %d %s\n",
                              TIME_FORMAT.format(System.currentTimeMillis()), getLevel(level), tag, pid, line)
        }

    }
}