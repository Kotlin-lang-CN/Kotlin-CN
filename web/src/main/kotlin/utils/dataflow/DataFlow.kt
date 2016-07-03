package utils.dataflow

import org.apache.log4j.Logger

/***
 * 逻辑安全, 表征函数体本身不会主动抛出业务逻辑异常
 * 使用该类函数不用考虑异常处理
 */
@MustBeDocumented @Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BusinessSafe()

/***
 * require expression
 */
@Throws(BusinessError::class)
inline fun <R> R.require(message: String = "default", status: Int = 400, filter: (R) -> Boolean): R = try {
    if (!filter(this)) throw BusinessError(message, status) else this@require
} catch(e: Throwable) {
    throw BusinessError(message, status)
}

/***
 * forbid expression
 */
@Throws(BusinessError::class)
inline fun <R> R.forbid(message: String = "default", status: Int = 400, filter: (R) -> Boolean): R = try {
    if (filter(this)) throw BusinessError(message, status) else this@forbid
} catch (e: Throwable) {
    throw BusinessError(message, status)
}

/***
 * try expression
 */
@Throws(BusinessError::class)
inline fun <T, R> T.monitor(message: String, status: Int = 400, log: Boolean = false, filter: (T) -> R): R = try {
    filter(this)
} catch (e: Throwable) {
    if (log) Logger.getLogger("monitor").info("error", e)
    throw BusinessError(message, status)
}


infix fun <R> R.next(action: (R) -> Unit): R {
    action.invoke(this)
    return this
}

/***
 * 主动抛出的业务逻辑错误
 */
class BusinessError(val msg: String, val status: Int) : RuntimeException(msg)