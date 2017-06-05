package tech.kotlin.utils

import tech.kotlin.common.os.Abort

enum class Err(val code: Int, val msg: String) {
    SYSTEM(1, "系统错误"),
    PARAMETER(2, "参数错误"),
    TOKEN_FAIL(3, "非法请求"),
    UNAUTHORIZED(4, "用户权限限制"),
    LOGIN_EXPIRE(5, "登录过期"),
    USER_BAN(6, "用户被冻结"),
    NEED_ACTIVATE_EMAIL(7, "需要验证邮箱"),
    GITHUB_AUTH_ERR(8, "无效的github token"),
    GITHUB_BIND_ALREADY(9, "该github账号已绑定账号"),
    USER_NAME_EXISTS(30, "用户已存在"),
    USER_EMAIL_EXISTS(31, "用户邮箱已经存在"),
    USER_NOT_EXISTS(32, "用户不存在"),
    ILLEGAL_PASSWORD(33, "密码错误"),
    ARTICLE_NOT_EXISTS(34, "文章不存在"),
    REPLY_NOT_EXISTS(35, "评论不存在"),
    ACTIVATE_EMAIL_ALREADY(36, "用户已绑定邮箱"),
    ILLEGAL_EMAIL_ACTIVATE_CODE(37, "无效的邮箱验证码"),

}

/**
 * Always throws [Abort] stating that operation will cause abort
 */
fun abort(err: Err, msg: String = ""): Nothing {
    throw Abort(err.code, if (msg.isNullOrBlank()) err.msg else msg)
}

@Throws(Abort::class)
inline fun <R> R.check(err: Err, msg: String = "", filter: (R) -> Boolean): R = try {
    if (!filter(this)) abort(err, msg) else this@check
} catch(e: Throwable) {
    abort(err, msg)
}

/***
 * try expression
 */
@Throws(Abort::class)
inline fun <T, R> T.tryExec(err: Err, msg: String = "", filter: (T) -> R): R = try {
    filter(this)
} catch (e: Throwable) {
    abort(err, msg)
}