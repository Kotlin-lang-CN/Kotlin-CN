package tech.kotlin.china.restful.utils

/***
 * execute with default return
 */
fun <R> safeMode(action: () -> R?): R? = try {
    action.invoke()
} catch(e: Throwable) {
    e.printStackTrace()
    null
}

fun <R> defaultMode(default: R, action: () -> R): R = try {
    action.invoke()
} catch(e: Throwable) {
    e.printStackTrace()
    default
}

/***
 * 使用 try catch 来检测执行一段业务代码
 */
@Throws(BusinessError::class)
fun <R> R.require(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (!filter(this)) throw BusinessError(message, status) else this
} catch(e: Throwable) {
    throw BusinessError(message, status)
}

fun <R> R.forbid(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (filter(this)) throw BusinessError(message, status) else this
} catch (e: Throwable) {
    throw BusinessError(message, status)
}

/***
 * 主动抛出的业务逻辑错误
 */
class BusinessError(val msg: String, val status: Int) : RuntimeException(msg)

