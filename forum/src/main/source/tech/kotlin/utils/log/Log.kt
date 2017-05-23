package tech.kotlin.utils.log

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

    val LOG_LEVEL = VERBOSE or DEBUG or INFO or WARN or ERROR

    //时间格式
    private val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA)

    private val sLogger = AppLogger()

    //verbose level
    fun v(tag: String?, message: String?): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message)
    }

    fun v(tag: String?, message: Long): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message.toString())
    }

    fun v(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, getDetail(e))
    }

    fun v(message: String?): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message)
    }

    fun v(message: Long): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, message.toString())
    }

    fun v(e: Throwable): Boolean {
        return !isLevelLogging(VERBOSE) || log(VERBOSE, tag, getDetail(e))
    }

    //debug level
    fun d(tag: String?, message: String?): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message)
    }

    fun d(tag: String?, message: Long): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message.toString())
    }

    fun d(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, getDetail(e))
    }

    fun d(message: String?): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message)
    }

    fun d(message: Long): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, message.toString())
    }

    fun d(e: Throwable): Boolean {
        return !isLevelLogging(DEBUG) || log(DEBUG, tag, getDetail(e))
    }

    //info level
    fun i(tag: String?, message: String?): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message)
    }

    fun i(tag: String?, message: Long): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message.toString())
    }

    fun i(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, getDetail(e))
    }

    fun i(message: String?): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message)
    }

    fun i(message: Long): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, message.toString())
    }

    fun i(e: Throwable): Boolean {
        return !isLevelLogging(INFO) || log(INFO, tag, getDetail(e))
    }

    //warn level
    fun w(tag: String?, message: String?): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message)
    }

    fun w(tag: String?, message: Long): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message.toString())
    }

    fun w(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, getDetail(e))
    }

    fun w(message: String?): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message)
    }

    fun w(message: Long): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, message.toString())
    }

    fun w(e: Throwable): Boolean {
        return !isLevelLogging(WARN) || log(WARN, tag, getDetail(e))
    }

    //error level
    fun e(tag: String?, message: String?): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message)
    }

    fun e(tag: String?, message: Long): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message.toString())
    }

    fun e(tag: String?, e: Throwable): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, getDetail(e))
    }

    fun e(message: String?): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message)
    }

    fun e(message: Long): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, message.toString())
    }

    fun e(e: Throwable): Boolean {
        return !isLevelLogging(ERROR) || log(ERROR, tag, getDetail(e))
    }

    //private methods
    private fun isLevelLogging(level: Int): Boolean {
        return level != DISABLED && level and LOG_LEVEL == level
    }

    private val tag: String?
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

    private fun getDetail(e: Throwable?): String? {
        if (e == null) return null
        return StringBuilder(e.toString()).apply {
            e.stackTrace.forEach { append(it).append("\n") }
        }.toString()
    }

    private fun log(level: Int, tag: String?, message: String?): Boolean {
        sLogger.log(level, tag, message)
        return true
    }

    //app file logger
    private class AppLogger {

        fun getLevel(level: Int): String? {
            when (level) {
                VERBOSE -> return "V"
                DEBUG -> return "D"
                INFO -> return "I"
                WARN -> return "W"
                ERROR -> return "E"
            }
            return "NULL"
        }

        fun log(level: Int, tag: String?, line: String?) {
            if (line.isNullOrBlank()) return
            System.out.printf("%s %s/%s %s\n",
                    TIME_FORMAT.format(System.currentTimeMillis()), getLevel(level), tag, line)
        }
    }
}