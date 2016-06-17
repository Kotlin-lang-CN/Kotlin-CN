package tech.kotlin.china.restful.utils

import tech.kotlin.china.restful.model.BusinessSafe
import java.util.*

/***
 * Map Generator
 */
fun <K, M : HashMap<K, Any>> M.p(k: K, v: Any): M {
    put(k, v)
    return this
}

class Maps {
    companion object {
        @BusinessSafe
        fun <K> p(k: K, v: Any): HashMap<K, Any> = HashMap<K, Any>().p(k, v)
    }
}


