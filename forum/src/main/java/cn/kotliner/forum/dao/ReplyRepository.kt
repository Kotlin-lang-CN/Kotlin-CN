package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.ReplyMapper
import cn.kotliner.forum.dao.redis.ReplyCache
import cn.kotliner.forum.domain.Reply
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class ReplyRepository {

    @Resource private lateinit var session: SqlSession
    @Resource private lateinit var cache: ReplyCache

    fun getById(id: Long, useCache: Boolean): Reply? {
        if (useCache) {
            val cached = cache.getById(id)
            if (cached != null) return cached
        }
        val result = session[ReplyMapper::class].queryById(id)
        if (result != null && useCache) {
            cache.update(result)
        }
        return result
    }

    fun create(reply: Reply) {
        val mapper = session[ReplyMapper::class]
        mapper.insert(reply)
    }

    fun update(id: Long, args: HashMap<String, String>) {
        val mapper = session[ReplyMapper::class]
        cache.invalid(id)
        mapper.updateByArgs(args.apply { this["id"] = "$id" })
    }

    fun getByPool(replyPoolId: String): List<Reply> {
        val result = session[ReplyMapper::class].queryByPool(replyPoolId)
        result.forEach { cache.update(it) }
        return result
    }

    fun getCountByPoolId(replyPoolId: String): Int {
        return if (!replyPoolId.isBlank()) {
            session[ReplyMapper::class].queryCountByPool(replyPoolId)
        } else {
            session[ReplyMapper::class].queryCount()
        }
    }

    fun getByAuthor(author: Long): List<Reply> {
        val result = session[ReplyMapper::class].queryByAuthor(author)
        result.forEach { cache.update(it) }
        return result
    }

    fun getCountByAuthor(author: Long): Int {
        return session[ReplyMapper::class].queryCountByAuthor(author)
    }
}