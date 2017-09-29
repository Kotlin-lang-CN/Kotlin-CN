package cn.kotliner.forum.exceptions

import cn.kotliner.forum.service.Err

/**s
 * Always throws [Abort] stating that operation will cause abort
 */
fun abort(err: Err, msg: String = ""): Nothing {
    throw Abort(err.code, if (msg.isBlank()) err.msg else msg)
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