package utils.map

import utils.dataflow.next
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

infix fun <T : Any> T.expose(fields: Array<String>) = HashMap<String, Any?>() next {
    fields.map { this.javaClass.getDeclaredField(it) }.forEach { f ->
        f.isAccessible = true
        it.put(f.name, f.get(this))
    }
}

fun Any.toMap() = HashMap<String, Any?>() next {
    this.javaClass.declaredFields.forEach { f ->
        f.isAccessible = true
        it.put(f.name, f.get(this))
    }
}