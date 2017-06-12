package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.model.domain.Account
import tech.kotlin.common.serialize.Json
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.get
import tech.kotlin.utils.Redis

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountDao {

    init {
        Mysql.register(AccountMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long, useCache: Boolean = false, updateCache: Boolean = false): Account? {
        if (useCache) {
            val cached = Cache.getById(id)
            if (cached != null) return cached
            val result = session[AccountMapper::class].getById(id)
            if (result != null && updateCache) Cache.update(result)
            return result
        } else {
            return session[AccountMapper::class].getById(id)
        }
    }

    fun saveOrUpdate(session: SqlSession, account: Account) {
        val mapper = session[AccountMapper::class]
        val mayBeNull = mapper.getById(account.id)
        if (mayBeNull == null) {
            mapper.insert(account)
        } else {
            Cache.drop(account.id)
            mapper.update(account)
        }
    }

    fun update(session: SqlSession, uid: Long, args: HashMap<String, String>) {
        val mapper = session[AccountMapper::class]
        Cache.drop(uid)
        mapper.updateWithArgs(args = args.apply { this["id"] = "$uid" })
    }

    internal object Cache {

        fun key(uid: Long) = "account:$uid"

        fun getById(uid: Long): Account? {
            val userMap = Redis read { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<Account>(userMap) else null
        }

        fun update(account: Account) {
            val key = key(account.id)
            Redis write {
                Json.reflect(account) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun drop(uid: Long) {
            val key = key(uid)
            Redis write { Json.reflect<Account> { name, _ -> it.hdel(key, name) } }
        }
    }

    interface AccountMapper {

        @Select("""
        SELECT  * FROM account
        WHERE id = #{id}
        LIMIT 1
        """)
        @Results(
                Result(property = "lastLogin", column = "last_login"),
                Result(property = "createTime", column = "create_time")
        )
        fun getById(id: Long): Account?

        @Insert("""
        INSERT INTO account
        VALUES
        (#{id}, #{password}, #{lastLogin}, #{state}, #{role}, #{createTime})
        """)
        fun insert(account: Account)

        @Update("""
        UPDATE account SET
        password = #{password},
        last_login = #{lastLogin},
        state = #{state},
        role = #{role},
        create_time = #{createTime}
        WHERE
        id = #{id}
        """)
        fun update(account: Account)

        @UpdateProvider(type = SQLGen::class, method = "updateWithArgs")
        fun updateWithArgs(args: Map<String, Any>)

        class SQLGen {

            fun updateWithArgs(args: Map<String, Any>): String {
                return """
                UPDATE account SET
                ${StringBuilder().apply {
                    args.forEach { k, _ -> append("$k = #{$k}, ") }
                    setLength(length - ", ".length)
                }}
                WHERE id = #{id}
                """
            }
        }
    }
}

