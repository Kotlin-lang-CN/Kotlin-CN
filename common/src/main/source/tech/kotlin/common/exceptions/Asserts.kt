package tech.kotlin.common.exceptions


/**
 * Always throws [Abort] stating that operation will cause abort
 */
fun abort(err: Err, msg: String = ""): Nothing {
    throw Abort(err.code, if (msg.isNullOrBlank()) err.msg else msg)
}


@Throws(Abort::class)
inline fun <R> R.require(err: Err, msg: String = "", filter: (R) -> Boolean): R = try {
    if (!filter(this)) throw Abort(err.code, msg) else this@require
} catch(e: Throwable) {
    throw Abort(err.code, msg)
}

/***
 * try expression
 */
@Throws(Abort::class)
inline fun <T, R> T.tryExec(err: Err, msg: String = "", filter: (T) -> R): R = try {
    filter(this)
} catch (e: Throwable) {
    throw Abort(err.code, msg)
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
