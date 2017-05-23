package tech.kotlin.utils.redis

import com.fasterxml.jackson.annotation.JsonProperty
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import tech.kotlin.utils.properties.Props


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Redis {

    lateinit var readWritePool: JedisPool

    fun init(config: String) {
        val conf = Props.loadJson<RedisConfig>(config)
        readWritePool = JedisPool(JedisPoolConfig().apply {
            maxIdle = conf.master.maxConn; maxTotal = conf.master.maxConn
        }, conf.master.host, conf.master.port)
    }

    inline fun <reified T> eval(vararg args: Any?, crossinline action: (Jedis) -> String): T {
        val strKeys = ArrayList<String>()
        val strArgs = args.map { if (it == null) null else "$it" }.toList()
        return readWritePool.resource.use { it.eval(action(it), strKeys, strArgs) as T }
    }

    operator fun <T> invoke(action: (Jedis) -> T): T = readWritePool.resource.use(action)

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