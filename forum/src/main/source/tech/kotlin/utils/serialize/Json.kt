package tech.kotlin.utils.serialize

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.reflect.Field


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Json {

    @JvmField
    val mapper: ObjectMapper = ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(KotlinModule())

    fun dumps(any: Any): String = mapper.writeValueAsString(any)

    inline fun <reified T : Any> loads(str: String): T = mapper.readValue<T>(str)

    fun <T> loads(str: String, typeRef: TypeReference<T>): T {
        return mapper.readValue(str, typeRef)
    }

    fun <T : Any> copy(data: T): T {
        return mapper.readValue(mapper.writeValueAsString(data), data.javaClass)
    }

    inline fun <reified T : Any> rawConvert(raw: Map<String, Any>): T {
        return loads(dumps(raw))
    }

    inline fun <T : Any> reflect(obj: T, action: (obj: T, name: String, field: Field) -> Unit) {
        obj.javaClass.declaredFields.filter { it.isAnnotationPresent(JsonProperty::class.java) }.forEach {
            val name = it.getAnnotation(JsonProperty::class.java).value
            it.isAccessible = true
            action(obj, name, it)
        }
    }

    inline fun <reified T : Any> reflect(action: (name: String, field: Field) -> Unit) {
        T::class.java.declaredFields.filter { it.isAnnotationPresent(JsonProperty::class.java) }.forEach {
            val name = it.getAnnotation(JsonProperty::class.java).value
            it.isAccessible = true
            action(name, it)
        }
    }
}