package tech.kotlin.china.restful.framework

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import java.util.*

object Config : ApplicationListener<ApplicationStartedEvent> {

    val properties: Properties by lazy {
        Config.javaClass.getResourceAsStream("/test/config.properties").use {
            val R = Properties()
            R.load(it)
            return@use R
        }
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        properties
    }

    fun get(name: String): String = properties.getProperty(name)

}