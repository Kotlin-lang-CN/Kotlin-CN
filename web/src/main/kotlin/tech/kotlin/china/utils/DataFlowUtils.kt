package tech.kotlin.china.utils


/***
 * 用以表述返回值的描述注解
 */
@MustBeDocumented @Target(AnnotationTarget.EXPRESSION) @Retention(AnnotationRetention.RUNTIME) annotation class Return

/***
 * 为 参变量提供注释
 */
@MustBeDocumented @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Doc(val value: String)

/***
 * 逻辑安全, 表征函数体本身不会主动抛出业务逻辑异常
 * 使用该类函数不用考虑异常处理
 */
@MustBeDocumented @Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BusinessSafe()

/***
 * 业务流数据流的校验方法
 */
@Throws(BusinessError::class)
inline fun <R> R.require(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (!filter(this)) throw BusinessError(message, status) else this
} catch(e: Throwable) {
    throw BusinessError(message, status)
}

@Throws(BusinessError::class)
inline fun <R> R.forbid(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (filter(this)) throw BusinessError(message, status) else this
} catch (e: Throwable) {
    throw BusinessError(message, status)
}

infix inline fun <R> R.next(action: (R) -> Unit): R {
    action.invoke(this)
    return this
}

/***
 * 主动抛出的业务逻辑错误
 */
class BusinessError(val msg: String, val status: Int) : RuntimeException(msg)