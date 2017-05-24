package tech.kotlin.utils.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Pipeline
import tech.kotlin.utils.properties.int
import tech.kotlin.utils.properties.str
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Redis {

    lateinit var pool: JedisPool

    fun init(prop: Properties) {
        pool = JedisPool(JedisPoolConfig(), prop str "redis.host", prop int "redis.port")
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