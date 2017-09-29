package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class UserInfoCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(uid: Long) = "user_info:$uid"

    fun getById(uid: Long): UserInfo? {
        val userMap = redis.boundHashOps<String, String>(key(uid)).entries()
        return if (!userMap.isEmpty()) Json.rawConvert<UserInfo>(userMap) else null
    }

    fun update(userInfo: UserInfo) {
        val map = HashMap<String, String>()
        Json.reflect(userInfo) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(userInfo.uid)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 2, TimeUnit.HOURS)
    }

    fun invalid(uid: Long) {
        redis.delete(key(uid))
    }

}