package tech.kotlin.china.restful.model

/***
 * 为 Controller 中的参变量提供注释
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Doc(val value: String)

/***
 * 逻辑安全, 表征函数体本身不会主动抛出业务逻辑异常
 * 使用该类函数不用考虑异常处理
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BusinessSafe()