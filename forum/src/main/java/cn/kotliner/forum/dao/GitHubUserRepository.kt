package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.GithubUserMapper
import cn.kotliner.forum.dao.redis.GithubUserCache
import cn.kotliner.forum.domain.model.GithubUser
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GitHubUserRepository {

    @Autowired private lateinit var session: SqlSession
    @Autowired private lateinit var cache: GithubUserCache

    fun getById(id: Long, useCache: Boolean = false, updateCache: Boolean = false): GithubUser? {
        if (useCache) {
            val cached = cache.getById(id)
            if (cached != null) return cached
            val result = session[GithubUserMapper::class].getById(id)
            if (result != null && updateCache) cache.update(result)
            return result
        } else {
            return session[GithubUserMapper::class].getById(id)
        }
    }

    fun saveOrUpdate(user: GithubUser) {
        val mapper = session[GithubUserMapper::class]
        val mayBeNull = mapper.getById(user.id)
        if (mayBeNull == null) {
            mapper.insert(user)
        } else {
            mapper.update(user)
            cache.invalid(user.id)
        }
    }

}