package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.model.Reply
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class ReplyCache {

    @Resource private lateinit var redis: StringRedisTemplate

    fun key(id: Long) = "reply:$id"

    fun getById(aid: Long): Reply? {
        val replyMap = redis.boundHashOps<String, String>(key(aid)).entries()
        return if (!replyMap.isEmpty()) Json.rawConvert<Reply>(replyMap) else null
    }

    fun update(reply: Reply) {
        val map = HashMap<String, String>()
        Json.reflect(reply) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(reply.id)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 1, TimeUnit.DAYS)
    }

    fun invalid(aid: Long) {
        redis.delete(key(aid))
    }

}