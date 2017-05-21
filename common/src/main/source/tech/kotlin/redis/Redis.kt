package tech.kotlin.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import tech.kotlin.Config
import tech.kotlin.RedisConf
import java.util.*


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Redis {

    val rand = Random()

    lateinit var readWritePool: JedisPool
    lateinit var readOnlyPool: List<JedisPool>

    fun init(name: String) {
        val config: RedisConf = Config[name]!!.redis
        readWritePool = JedisPool(JedisPoolConfig().apply {
            maxIdle = config.master.maxConn; maxTotal = config.master.maxConn
        }, config.master.host, config.master.port)
        readOnlyPool = ArrayList<JedisPool>().apply {
            addAll(config.slave.map {
                JedisPool(JedisPoolConfig().apply {
                    maxTotal = it.maxConn; maxIdle = it.maxConn
                }, it.host, it.port)
            }.toList())
        }
    }

    inline fun <reified T> eval(vararg args: Any?, crossinline action: (Jedis) -> String): T {
        val strKeys = ArrayList<String>()
        val strArgs = args.map { if (it == null) null else "$it" }.toList()
        return readWritePool.resource.use { it.eval(action(it), strKeys, strArgs) as T }
    }

    infix fun <T> write(action: (Jedis) -> T): T = readWritePool.resource.use(action)

    infix fun <T> writeNullable(action: (Jedis) -> T?): T? = readWritePool.resource.use(action)

    infix fun <T> read(action: (Jedis) -> T): T {
        if (readOnlyPool.isEmpty()) {
            return write(action)
        } else {
            return readOnlyPool[rand.nextInt(readOnlyPool.size)].resource.use(action)
        }
    }

    infix fun <T> readNullable(action: (Jedis) -> T?): T? {
        if (readOnlyPool.isEmpty()) {
            return write(action)
        } else {
            return readOnlyPool[rand.nextInt(readOnlyPool.size)].resource.use(action)
        }
    }
}