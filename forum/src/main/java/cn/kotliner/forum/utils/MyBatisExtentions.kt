package cn.kotliner.forum.utils

import cn.kotliner.forum.utils.algorithm.Json
import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

internal val log: Logger = LoggerFactory.getLogger(SqlSession::class.java)

//为 Mybatis ORM 添加自定义的日志
operator fun <T : Any> SqlSession.get(kClass: KClass<T>): T {
    val clazz = kClass.java
    val mapper = getMapper(clazz)
    @Suppress("UNCHECKED_CAST")
    return Proxy.newProxyInstance(javaClass.classLoader, kotlin.arrayOf(clazz)) { _, method, args ->
        try {
            val sqlGen: (KClass<*>, String) -> String = { clazz, methodName ->
                val generator = clazz.java.newInstance()
                val provider = clazz.java.getMethod(methodName, *method.parameterTypes)
                provider(generator, *args) as String
            }
            when {
                method.isAnnotationPresent(Select::class.java) ->
                    log.debug("SQL", method.getAnnotation(Select::class.java).value[0].trimIndent()
                            .replace("\n", " "))
                method.isAnnotationPresent(Insert::class.java) ->
                    log.debug("SQL", method.getAnnotation(Insert::class.java).value[0].trimIndent()
                            .replace("\n", " "))
                method.isAnnotationPresent(Update::class.java) ->
                    log.debug("SQL", method.getAnnotation(Update::class.java).value[0].trimIndent()
                            .replace("\n", " "))
                method.isAnnotationPresent(Delete::class.java) ->
                    log.debug("SQL", method.getAnnotation(Delete::class.java).value[0].trimIndent()
                            .replace("\n", " "))
                method.isAnnotationPresent(SelectProvider::class.java) -> {
                    val annotation = method.getAnnotation(SelectProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    log.debug("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(InsertProvider::class.java) -> {
                    val annotation = method.getAnnotation(InsertProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    log.debug("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(UpdateProvider::class.java) -> {
                    val annotation = method.getAnnotation(UpdateProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    log.debug("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(DeleteProvider::class.java) -> {
                    val annotation = method.getAnnotation(DeleteProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    log.debug("SQL", sql.trimIndent().replace("\n", " "))
                }
            }
        } catch (err: Throwable) {
            log.error(err.toString(), err)
        }
        if (args == null || args.isEmpty()) {
            method.invoke(mapper)
        } else {
            log.debug("SQL", "args:${Json.dumps(args)}")
            method.invoke(mapper, *args)
        }
    } as T
}