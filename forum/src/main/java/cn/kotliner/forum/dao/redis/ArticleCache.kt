package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.model.Article
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Component
class ArticleCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(id: Long) = "article:$id"

    fun getById(aid: Long): Article? {
        val userMap = redis.boundHashOps<String, String>(key(aid)).entries()
        return if (!userMap.isEmpty()) Json.rawConvert<Article>(userMap) else null
    }

    fun update(article: Article) {
        val map = HashMap<String, String>()
        Json.reflect(article) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(article.id)
        redis.boundHashOps<String, String>(key).putAll(map)
        redis.expire(key, 2, TimeUnit.HOURS)
    }

    fun invalid(aid: Long) {
        redis.delete(key(aid))
    }

}