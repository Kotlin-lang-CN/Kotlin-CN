package cn.kotliner.forum.dao.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class FlowerCountCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(flowerPoolId: String) = "flower:count:$flowerPoolId"

    operator fun set(flowerPoolId: String, count: Int) {
        redis.boundValueOps(key(flowerPoolId)).set("$count")
    }

    operator fun get(flowerPoolId: String): Int {
        return redis.boundValueOps(key(flowerPoolId)).get()?.toInt() ?: -1
    }

    fun invalid(flowerPoolId: String) {
        redis.delete(key(flowerPoolId))
    }

}