package tech.kotlin.utils

import org.apache.ibatis.annotations.*
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Pipeline
import tech.kotlin.common.os.Log
import tech.kotlin.common.os.Abort
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.int
import tech.kotlin.common.utils.str
import tech.kotlin.service.ServDef
import java.io.BufferedReader
import java.io.InputStreamReader
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

object Redis {
    lateinit var pool: JedisPool

    fun init(prop: Properties) {
        pool = JedisPool(JedisPoolConfig(),
                prop str "redis.${ServDef.ARTICLE}.host",
                prop int "redis.${ServDef.ARTICLE}.port")
    }

    infix fun <T> read(action: (Jedis) -> T): T {
        return pool.resource.use(action)
    }

    infix fun write(action: (Pipeline) -> Unit) {
        pool.resource.use {
            val pip = it.pipelined()
            action(pip)
            pip.sync()
        }
    }
}

object Http : HttpClient(SslContextFactory()) {
    init {
        isFollowRedirects = false
        start()
    }
}

//为 Mybatis ORM 添加自定义的日志
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
        } catch (err: Throwable) {
            Log.e(err)
        }
        if (args == null || args.isEmpty()) {
            method.invoke(mapper)
        } else {
            Log.d("SQL", "args:${Json.dumps(args)}")
            method.invoke(mapper, *args)
        }
    } as T
}