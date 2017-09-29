package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.ArticleMapper
import cn.kotliner.forum.dao.redis.ArticleCache
import cn.kotliner.forum.domain.Article
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class ArticleRepository {

    @Resource private lateinit var session: SqlSession
    @Resource private lateinit var cache: ArticleCache

    fun getById(id: Long, useCache: Boolean): Article? {
        if (useCache) {
            val cached = cache.getById(id)
            if (cached != null) return cached
            val result = session[ArticleMapper::class].queryById(id)
            if (result != null && useCache) cache.update(result)
            return result
        } else {
            return session[ArticleMapper::class].queryById(id)
        }
    }

    fun getLatest(args: HashMap<String, String>, updateCache: Boolean = false): List<Article> {
        val result = session[ArticleMapper::class].queryLatest(args)
        if (updateCache) result.forEach { cache.update(it) }
        return result
    }

    fun getByAuthor(id: Long, useCache: Boolean): List<Article> {
        val result = session[ArticleMapper::class].queryByAuthor(id)
        if (useCache) result.forEach { cache.update(it) }
        return result
    }

    fun countByAuthor(id: Long): Int {
        return session[ArticleMapper::class].queryCountByAuthor(id)
    }

    fun countAll(): Int {
        return session[ArticleMapper::class].queryAll()
    }

    fun createOrUpdate(article: Article) {
        val mapper = session[ArticleMapper::class]
        if (mapper.queryById(article.id) == null) {
            mapper.insert(article)
        } else {
            cache.invalid(article.id)
            mapper.update(article)
        }
    }

    fun update(id: Long, args: HashMap<String, Any>) {
        val mapper = session[ArticleMapper::class]
        cache.invalid(id)
        mapper.updateByArgs(args.apply { this["id"] = id })
    }
}