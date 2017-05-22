package tech.kotlin.common.exceptions


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


/***
 * for object hashcode compare
 */
object Objs {
    @JvmStatic
    fun isEqual(obj1: Any?, obj2: Any?): Boolean {
        return obj1 == null && obj2 == null || obj1 != null && obj1 == obj2
    }
}
