package tech.kotlin.utils.redis

import com.fasterxml.jackson.annotation.JsonProperty
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import tech.kotlin.utils.properties.Props
import java.util.*
import kotlin.collections.ArrayList


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Redis {

    val rand = Random()

    lateinit var readWritePool: JedisPool
    lateinit var readOnlyPool: List<JedisPool>

    fun init(config: String) {
        val conf = Props.loadJson<RedisConfig>(config)
        readWritePool = JedisPool(JedisPoolConfig().apply {
            maxIdle = conf.master.maxConn; maxTotal = conf.master.maxConn
        }, conf.master.host, conf.master.port)
        readOnlyPool = ArrayList<JedisPool>().apply {
            addAll(conf.slave.map {
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

    infix fun <T> read(action: (Jedis) -> T): T {
        if (readOnlyPool.isEmpty()) {
            return write(action)
        } else {
            return readOnlyPool[rand.nextInt(readOnlyPool.size)].resource.use(action)
        }
    }

    class RedisConfig {
        @JsonProperty("master") var master = Address()
        @JsonProperty("slave") var slave = ArrayList<Address>()
    }

    class Address {
        @JsonProperty("host") var host = ""
        @JsonProperty("port") var port = 0
        @JsonProperty("max_conn") var maxConn = 20
    }
}