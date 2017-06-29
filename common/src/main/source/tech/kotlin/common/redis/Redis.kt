package tech.kotlin.common.redis

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster
import tech.kotlin.common.os.Abort
import tech.kotlin.common.utils.str
import java.util.*
import kotlin.properties.Delegates


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Redis {

    var cluster by Delegates.notNull<JedisCluster>()

    fun init(prop: Properties) {
        cluster = JedisCluster(HashSet<HostAndPort>().apply {
            for (i in 1..100) {
                try {
                    val config = prop str "redis.cluster.$i"
                    val host = config.split(":")[0]
                    val port = config.split(":")[1].toInt()
                    add(HostAndPort(host, port))
                } catch (abort: Abort) {
                    break
                }
            }
        })
    }

    operator fun <T> invoke(action: (JedisCluster) -> T): T {
        return cluster.run { action(this) }
    }
}