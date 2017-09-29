package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.ProfileMapper
import cn.kotliner.forum.dao.redis.ProfileCache
import cn.kotliner.forum.domain.Profile
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProfileRepository {

    @Autowired private lateinit var session: SqlSession
    @Autowired private lateinit var cache: ProfileCache

    fun getById(uid: Long, useCache: Boolean): Profile? {
        if (useCache) {
            val cached = cache.getById(uid)
            if (cached != null) return cached
            val result = session[ProfileMapper::class].getById(uid)
            if (result != null && useCache) cache.update(result)
            return result
        } else {
            return session[ProfileMapper::class].getById(uid)
        }
    }

    fun createOrUpdate(profile: Profile) {
        val mapper = session[ProfileMapper::class]
        val mayBeNull = mapper.getById(profile.uid)
        if (mayBeNull == null) {
            mapper.insert(profile)
        } else {
            mapper.update(profile)
            cache.invalid(profile.uid)
        }
    }

}