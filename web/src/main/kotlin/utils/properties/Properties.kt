package utils.properties

import java.util.*

fun Properties.p(k: String, v: Any?, default: () -> Any? = { Nothing() }): Properties {
    put(k, if (v == null && default() !is Nothing) default() else v)
    return this
}

class Props {
    companion object {
        fun p(k: String, v: Any?, default: () -> Any? = { Nothing() }): Properties = Properties().p(k, v, default)
    }
}

private class Nothing

object Env {
    operator fun get(name: String) = System.getenv()[name]
}