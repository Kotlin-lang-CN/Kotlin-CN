package tech.kotlin.china.utils

object Env {
    operator fun get(name: String) = System.getenv()[name]
}

