package tech.kotlin.utils.mysql

import org.apache.ibatis.annotations.*
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tech.kotlin.utils.exceptions.Abort
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.log.Log
import tech.kotlin.utils.serialize.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.reflect.KClass


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Mysql {

    lateinit var sqlSessionFactory: SqlSessionFactory

    fun init(config: String, properties: Properties, sql: String = "") {
        val inputStream = Resources.getResourceAsStream(config)
        sqlSessionFactory = SqlSessionFactoryBuilder().build(inputStream, properties)

        if (sql.isNullOrBlank()) return

        write { session ->
            // Initialize object for ScriptRunner
            val runner = ScriptRunner(session.connection)
            // Give the input file to Reader
            BufferedReader(InputStreamReader(javaClass.classLoader.getResourceAsStream(sql.trimStart('/')))).use {
                runner.runScript(it)
            }
        }
    }

    fun register(mapper: Class<*>) {
        sqlSessionFactory.configuration.addMapper(mapper)
    }

    inline fun <T> read(defaultError: Err = Err.SYSTEM, action: (SqlSession) -> T): T {
        try {
            return sqlSessionFactory.openSession(true).use(action)
        } catch (error: Abort) {
            throw error
        } catch (error: Exception) {
            Log.e(error)
            abort(defaultError)
        }
    }

    inline fun <T> write(defaultError: Err = Err.SYSTEM, action: (SqlSession) -> T): T {
        sqlSessionFactory.openSession(false).use {
            try {
                val result = action(it)
                it.commit()
                return result
            } catch (error: Abort) {
                it.rollback()
                throw error
            } catch (error: Exception) {
                it.rollback()
                Log.e(error)
                abort(defaultError)
            }
        }
    }
}

operator fun <T : Any> SqlSession.get(kClass: KClass<T>): T {
    val clazz = kClass.java
    val mapper = getMapper(clazz)
    @Suppress("UNCHECKED_CAST")
    return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(clazz)) { _, method, args ->
        try {
            val sqlGen: (KClass<*>, String) -> String = { clazz, methodName ->
                val generator = clazz.java.newInstance()
                val provider = clazz.java.getMethod(methodName, *method.parameterTypes)
                provider(generator, *args) as String
            }
            when {
                method.isAnnotationPresent(Select::class.java) ->
                    Log.d("SQL", method.getAnnotation(Select::class.java).value[0].trimIndent().replace("\n", " "))
                method.isAnnotationPresent(Insert::class.java) ->
                    Log.d("SQL", method.getAnnotation(Insert::class.java).value[0].trimIndent().replace("\n", " "))
                method.isAnnotationPresent(Update::class.java) ->
                    Log.d("SQL", method.getAnnotation(Update::class.java).value[0].trimIndent().replace("\n", " "))
                method.isAnnotationPresent(Delete::class.java) ->
                    Log.d("SQL", method.getAnnotation(Delete::class.java).value[0].trimIndent().replace("\n", " "))
                method.isAnnotationPresent(SelectProvider::class.java) -> {
                    val annotation = method.getAnnotation(SelectProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    Log.d("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(InsertProvider::class.java) -> {
                    val annotation = method.getAnnotation(InsertProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    Log.d("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(UpdateProvider::class.java) -> {
                    val annotation = method.getAnnotation(UpdateProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    Log.d("SQL", sql.trimIndent().replace("\n", " "))
                }
                method.isAnnotationPresent(DeleteProvider::class.java) -> {
                    val annotation = method.getAnnotation(DeleteProvider::class.java)
                    val sql = sqlGen(annotation.type, annotation.method)
                    Log.d("SQL", sql.trimIndent().replace("\n", " "))
                }
            }
            Log.d("SQL", Json.dumps(args[0]))
        } catch (err: Throwable) {
            Log.e(err)
        }
        return@newProxyInstance method.invoke(mapper, *args)
    } as T
}