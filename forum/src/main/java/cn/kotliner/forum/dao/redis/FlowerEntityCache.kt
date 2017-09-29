package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.Flower
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class FlowerEntityCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(flowerPoolId: String, owner: Long) = "flower:$owner:$flowerPoolId"

    operator fun set(flowerPoolId: String, owner: Long, flower: Flower) {
        val map = HashMap<String, String>()
        Json.reflect(flower) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(flowerPoolId, owner)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 1, TimeUnit.DAYS)
    }

    operator fun get(flowerPoolId: String, owner: Long): Flower? {
        val flowerMap = redis.boundHashOps<String, String>(key(flowerPoolId, owner)).entries()
        return if (!flowerMap.isEmpty()) Json.rawConvert<Flower>(flowerMap) else null
    }

    fun invalid(flowerPoolId: String, owner: Long) {
        redis.delete(key(flowerPoolId, owner))
    }

}