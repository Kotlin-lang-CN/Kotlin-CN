package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.redis.AccountCache
import cn.kotliner.forum.dao.mysql.AccountMapper
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class AccountRepository {

    @Resource private lateinit var session: SqlSession
    @Resource private lateinit var cache: AccountCache

    fun getById(id: Long, useCache: Boolean): Account? {
        if (useCache) {
            val cached = cache.getById(id)
            if (cached != null) return cached
            val result = session[AccountMapper::class].getById(id)
            if (result != null && useCache) cache.update(result)
            return result
        } else {
            return session[AccountMapper::class].getById(id)
        }
    }

    fun saveOrUpdate(account: Account) {
        val mapper = session[AccountMapper::class]
        val mayBeNull = mapper.getById(account.id)
        if (mayBeNull == null) {
            mapper.insert(account)
        } else {
            mapper.update(account)
            cache.invalid(account.id)
        }
    }

    fun getCount(): Int {
        return session[AccountMapper::class].getCount()
    }


    fun update(uid: Long, args: HashMap<String, String>) {
        val mapper = session[AccountMapper::class]
        cache.invalid(uid)
        mapper.updateWithArgs(args = args.apply { this["id"] = "$uid" })
    }
}