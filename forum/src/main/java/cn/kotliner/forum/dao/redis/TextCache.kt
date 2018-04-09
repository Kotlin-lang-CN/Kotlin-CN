package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.model.TextContent
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class TextCache {

    @Resource private lateinit var redis: StringRedisTemplate

    fun key(id: Long) = "text_content:$id"

    fun getById(uid: Long): TextContent? {
        val userMap = redis.boundHashOps<String, String>(key(uid)).entries()
        return if (!userMap.isEmpty()) Json.rawConvert<TextContent>(userMap) else null
    }

    fun update(content: TextContent) {
        val map = HashMap<String, String>()
        Json.reflect(content) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(content.id)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 1, TimeUnit.DAYS)
    }

}