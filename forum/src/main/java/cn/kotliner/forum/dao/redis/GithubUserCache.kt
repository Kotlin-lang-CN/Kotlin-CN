package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.GithubUser
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class GithubUserCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(id: Long) = "github_user_info:$id"

    fun getById(id: Long): GithubUser? {
        val userMap = redis.boundHashOps<String, String>(key(id)).entries()
        return if (!userMap.isEmpty()) Json.rawConvert<GithubUser>(userMap) else null
    }

    fun update(githubUser: GithubUser) {
        val map = HashMap<String, String>()
        Json.reflect(githubUser) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(githubUser.id)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 2, TimeUnit.HOURS)
    }

    fun invalid(id: Long) {
        redis.delete(key(id))
    }
}