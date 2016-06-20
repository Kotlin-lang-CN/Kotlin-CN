package tech.kotlin.china.restful.component

import org.springframework.stereotype.Component
import java.util.*

@Component
open class Config {
    val properties: Properties = javaClass.getResourceAsStream("/config/config.properties").use {
        val R = Properties()
        R.load(it)
        return@use R
    }

    operator fun get(prop: String) = properties.getProperty(prop)
}