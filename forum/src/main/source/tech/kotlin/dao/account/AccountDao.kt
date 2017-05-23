package tech.kotlin.dao.account

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.model.domain.Account
import tech.kotlin.utils.serialize.Json
import tech.kotlin.utils.mysql.Mysql
import tech.kotlin.utils.redis.Redis

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
            val result = session.getMapper(AccountMapper::class.java).getById(id)
            if (result != null && updateCache) Cache.update(result)
            return result
        } else {
            return session.getMapper(AccountMapper::class.java).getById(id)
        }
    }

    fun saveOrUpdate(session: SqlSession, account: Account) {
        val mapper = session.getMapper(AccountMapper::class.java)
        val mayBeNull = mapper.getById(account.id)
        if (mayBeNull == null) {
            mapper.insert(account)
        } else {
            Cache.drop(account.id)
            mapper.update(account)
        }
    }

    fun update(session: SqlSession, uid: Long, args: HashMap<String, Any>) {
        val mapper = session.getMapper(AccountMapper::class.java)
        Cache.drop(uid)
        mapper.updateWithArgs(args = args.apply { this["id"] = uid })
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
                val pip = it.pipelined()
                Json.reflect(account) { obj, name, field -> pip.hset(key, name, "${field.get(obj)}") }
                pip.sync()
            }
        }

        fun drop(uid: Long) {
            val key = key(uid)
            Redis write {
                val pip = it.pipelined()
                Json.reflect<Account> { name, _ -> pip.hdel(key, name) }
                pip.sync()
            }
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
                    args.forEach { k, _ -> append("$k = #{$k} ,") }
                    setLength(length - ",".length)
                }}
                WHERE id = #{id}
                """
            }
        }
    }
}

