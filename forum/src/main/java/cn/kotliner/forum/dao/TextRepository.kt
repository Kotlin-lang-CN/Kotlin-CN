package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.TextMapper
import cn.kotliner.forum.dao.redis.TextCache
import cn.kotliner.forum.domain.TextContent
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class TextRepository {

    @Resource private lateinit var session: SqlSession
    @Resource private lateinit var cache: TextCache

    fun getById(id: Long): TextContent? {
        val cached = cache.getById(id)
        if (cached != null) return cached
        val result = session[TextMapper::class].queryById(id)
        if (result != null) cache.update(result)
        return result
    }

    fun create(content: TextContent) {
        val mapper = session[TextMapper::class]
        mapper.insert(content)
    }


}