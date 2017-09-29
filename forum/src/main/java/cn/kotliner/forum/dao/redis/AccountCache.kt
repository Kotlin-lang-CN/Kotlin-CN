package cn.kotliner.forum.dao.redis

import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class AccountCache {

    @Resource private lateinit var redis: StringRedisTemplate

    private fun key(uid: Long) = "account:$uid"

    fun getById(uid: Long): Account? {
        val userMap = redis.boundHashOps<String, String>(key(uid)).entries()
        return if (!userMap.isEmpty()) Json.rawConvert<Account>(userMap) else null
    }

    fun update(account: Account) {
        val map = HashMap<String, String>()
        Json.reflect(account) { obj, name, field -> map[name] = "${field.get(obj)}" }
        val key = key(account.id)
        redis.boundHashOps<String, String>(key).putAll(map)
    }

    fun invalid(uid: Long) {
        redis.delete(key(uid))
    }

}