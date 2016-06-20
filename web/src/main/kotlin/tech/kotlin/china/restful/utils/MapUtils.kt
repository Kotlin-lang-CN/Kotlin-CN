package tech.kotlin.china.restful.utils

import java.util.*

/***
 * Map Generator
 */
fun <K, M : HashMap<K, Any?>> M.p(k: K, v: Any?): M {
    put(k, v)
    return this
}

class Maps {
    companion object {
        fun <K> p(k: K, v: Any?): HashMap<K, Any?> = HashMap<K, Any?>().p(k, v)
    }
}

fun <T : Any> T.expose(vararg fields: String): HashMap<String, Any?> {
    val result = HashMap<String, Any?> ()
    fields.forEach {
        val f = this@expose.javaClass.getDeclaredField(it)
        f.isAccessible = true
        result[it] = f.get(this)
    }
    return result
}


