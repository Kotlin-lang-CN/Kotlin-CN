package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.Profile
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class ProfileCache {

    @Resource private lateinit var redis: StringRedisTemplate

    fun key(uid: Long) = "profile:$uid"

    fun getById(uid: Long): Profile? {
        val profileMap = redis.boundHashOps<String, Any>(key(uid)).entries()
        return if (!profileMap.isEmpty()) Json.rawConvert<Profile>(profileMap) else null
    }

    fun update(profile: Profile) {
        val map = HashMap<String, String>()
        Json.reflect(profile) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(profile.uid)
        redis.boundHashOps<String, Any>(key).putAll(map)
        redis.expire(key, 2, TimeUnit.HOURS)
    }

    fun invalid(uid: Long) {
        redis.delete(key(uid))
    }
}