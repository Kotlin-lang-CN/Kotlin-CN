package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.redis.Redis
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.mysql.get
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object UserInfoDao {

    init {
        Mysql.register(UserInfoMapper::class.java)
    }

    fun getById(session: SqlSession, uid: Long, useCache: Boolean): UserInfo? {
        if (useCache) {
            val cached = Cache.getById(uid)
            if (cached != null) return cached
            val result = session[UserInfoMapper::class].getById(uid)
            if (result != null && useCache) Cache.update(result)
            return result
        } else {
            return session[UserInfoMapper::class].getById(uid)
        }
    }

    fun getByName(session: SqlSession, name: String, updateCache: Boolean = false): UserInfo? {
        val result = session[UserInfoMapper::class].getByName(name)
        if (result != null && updateCache) Cache.update(result)
        return result
    }

    fun getByEmail(session: SqlSession, email: String, updateCache: Boolean = false): UserInfo? {
        val result = session[UserInfoMapper::class].getByEmail(email)
        if (result != null && updateCache) Cache.update(result)
        return result
    }

    fun saveOrUpdate(session: SqlSession, user: UserInfo) {
        val mapper = session[UserInfoMapper::class]
        val mayBeNull = mapper.getById(user.uid)
        if (mayBeNull == null) {
            mapper.insert(user)
        } else {
            mapper.update(user)
            Cache.drop(user.uid)
        }
    }

    fun update(session: SqlSession, uid: Long, args: HashMap<String, String>) {
        val mapper = session[UserInfoMapper::class]
        Cache.drop(uid)
        mapper.updateWithArgs(args = args.apply { this["uid"] = "$uid" })
    }

    private object Cache {

        fun key(uid: Long) = "user_info:$uid"

        fun getById(uid: Long): UserInfo? {
            val userMap = Redis { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<UserInfo>(userMap) else null
        }

        fun update(userInfo: UserInfo) {
            val key = key(userInfo.uid)
            Redis {
                val map = HashMap<String, String>()
                Json.reflect(userInfo) { obj, name, field -> map[name] = "${field.get(obj)}" }
                it.hmset(key, map)
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun drop(uid: Long) {
            Redis { it.del(key(uid)) }
        }
    }

    interface UserInfoMapper {

        @Select("""
        SELECT * FROM user_info
        WHERE uid = #{uid}
        LIMIT 1
        """)
        @Results(Result(column = "email_state", property = "emailState"))
        fun getById(uid: Long): UserInfo?

        @Select("""
        SELECT * FROM user_info
        WHERE username = #{username}
        LIMIT 1
        """)
        @Results(Result(column = "email_state", property = "emailState"))
        fun getByName(username: String): UserInfo?

        @Select("""
        SELECT * FROM user_info
        WHERE email= #{email}
        LIMIT 1
        """)
        @Results(Result(column = "email_state", property = "emailState"))
        fun getByEmail(email: String): UserInfo?

        @Insert("""
        INSERT INTO user_info
        VALUES
        (#{uid}, #{username}, #{logo}, #{email}, #{emailState})
        """)
        fun insert(user: UserInfo)

        @Update("""
        UPDATE user_info SET
        username = #{username},
        logo = #{logo},
        email = #{email},
        email_state = #{emailState}
        WHERE
        uid = #{uid}
        """)
        fun update(user: UserInfo)

        @UpdateProvider(type = SQLGenerator::class, method = "updateWithArgs")
        fun updateWithArgs(args: Map<String, String>)

        class SQLGenerator {

            fun updateWithArgs(args: Map<String, String>): String {
                return """
                UPDATE user_info SET
                ${StringBuilder().apply {
                    args.forEach { k, _ -> append("$k = #{$k}, ") }
                    setLength(length - ", ".length)
                }}
                WHERE uid = #{uid}
                """
            }
        }

    }
}
