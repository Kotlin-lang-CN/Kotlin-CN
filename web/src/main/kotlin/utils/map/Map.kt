package utils.map

import utils.dataflow.next
import java.util.*

/***
 * Map Generator
 */
open class Row : HashMap<String, Any?>()

fun p(k: String, v: Any?): Row = Row().p(k, v)

fun <T : Row> T.p(k: String, v: Any?): T {
    if (this is HashMap<String, Any?>) put(k, v)
    return this
}

fun <T : Any> T.hide(vararg fields: String) = Row() next {
    if (this is Row) {
        this.entries.forEach { f ->
            if (!fields.contains(f.key)) it.put(f.key, f.value)
        }
    } else {
        javaClass.declaredFields.forEach { f ->
            f.isAccessible = true
            if (!fields.contains(f.name)) it.put(f.name, f.get(this))
        }
    }
}

fun <T : Any> T.expose(vararg fields: String) = Row() next {
    if (this is Row) {
        fields.forEach { f -> it.put(f, this[f]) }
    } else {
        fields.map { this.javaClass.getDeclaredField(it) }.forEach { f ->
            f.isAccessible = true
            it.put(f.name, f.get(this))
        }
    }
}

fun Any.row() = hide()