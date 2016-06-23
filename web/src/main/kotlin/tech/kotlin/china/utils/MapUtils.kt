package tech.kotlin.china.utils

import java.util.*
import kotlin.reflect.KProperty1

/***
 * Map Generator
 */
fun <K> p(k: K, v: Any?): HashMap<K, Any?> = HashMap<K, Any?>().p(k, v)

fun <K, M : HashMap<K, Any?>> M.p(k: K, v: Any?): M {
    put(k, v)
    return this
}

operator fun <T : Any> T.get(vararg fields: KProperty1<T, Any?>) = HashMap<String, Any?>() next {
    fields.forEach { f -> it.p(f.name, f.get(this)) }
}

fun Properties.p(k: String, v: Any?, default: () -> Any? = { Nothing() }): Properties {
    val p = Properties()
    p.put(k, if (v == null && default() !is Nothing) default else v)
    return this
}

fun Any.toMap(): HashMap<String, Any?> = HashMap<String, Any?>() next  {
    javaClass.declaredFields.forEach { f ->
        f.isAccessible = true
        it.put(f.name, f.get(this))
    }
}

class Props {
    companion object {
        fun p(k: String, v: Any?, default: () -> Any? = { Nothing() }): Properties = Properties().p(k, v, default)
    }
}

private class Nothing
