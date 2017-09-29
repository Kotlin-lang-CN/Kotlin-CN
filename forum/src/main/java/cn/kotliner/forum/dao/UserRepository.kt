package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.UserInfoMapper
import cn.kotliner.forum.dao.redis.UserInfoCache
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserRepository {

    @Autowired lateinit var session: SqlSession
    @Autowired lateinit var cache: UserInfoCache

    fun getById(uid: Long, useCache: Boolean): UserInfo? {
        if (useCache) {
            val cached = cache.getById(uid)
            if (cached != null) return cached
            val result = session[UserInfoMapper::class].getById(uid)
            if (result != null && useCache) cache.update(result)
            return result
        } else {
            return session[UserInfoMapper::class].getById(uid)
        }
    }

    fun getByName(name: String, updateCache: Boolean = false): UserInfo? {
        val result = session[UserInfoMapper::class].getByName(name)
        if (result != null && updateCache) cache.update(result)
        return result
    }

    fun getByEmail(email: String, updateCache: Boolean = false): UserInfo? {
        val result = session[UserInfoMapper::class].getByEmail(email)
        if (result != null && updateCache) cache.update(result)
        return result
    }

    fun saveOrUpdate(user: UserInfo) {
        val mapper = session[UserInfoMapper::class]
        val mayBeNull = mapper.getById(user.uid)
        if (mayBeNull == null) {
            mapper.insert(user)
        } else {
            mapper.update(user)
            cache.invalid(user.uid)
        }
    }

    fun update(uid: Long, args: HashMap<String, String>) {
        val mapper = session[UserInfoMapper::class]
        mapper.updateWithArgs(args = args.apply { this["uid"] = "$uid" })
        cache.invalid(uid)
    }
}