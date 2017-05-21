package tech.kotlin.account.dao

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.serialize.Json
import tech.kotlin.model.UserInfo
import tech.kotlin.mysql.Mysql
import tech.kotlin.redis.Redis

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object UserInfoDao {

    init {
        Mysql.register(UserInfoMapper::class.java)
    }

    fun getById(session: SqlSession, uid: Long, useCache: Boolean = false, updateCache: Boolean = false): UserInfo? {
        if (useCache) {
            val cached = Cache.getById(uid)
            if (cached != null) return cached
            val result = session.getMapper(UserInfoMapper::class.java).getById(uid)
            if (result != null && updateCache) Cache.update(result)
            return result
        } else {
            return session.getMapper(UserInfoMapper::class.java).getById(uid)
        }
    }

    fun getByName(session: SqlSession, name: String, updateCache: Boolean = false): UserInfo? {
        val result = session.getMapper(UserInfoMapper::class.java).getByName(name)
        if (result != null && updateCache) Cache.update(result)
        return result
    }

    fun getByEmail(session: SqlSession, email: String, updateCache: Boolean = false): UserInfo? {
        val result = session.getMapper(UserInfoMapper::class.java).getByEmail(email)
        if (result != null && updateCache) Cache.update(result)
        return result
    }

    fun saveOrUpdate(session: SqlSession, user: UserInfo) {
        val mapper = session.getMapper(UserInfoMapper::class.java)
        val mayBeNull = mapper.getById(user.uid)
        if (mayBeNull == null) {
            mapper.insert(user)
        } else {
            Cache.drop(user.uid)
            mapper.update(user)
        }
    }

    internal object Cache {

        fun key(uid: Long) = "user_info:$uid"

        fun getById(uid: Long): UserInfo? {
            val userMap = Redis read { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<UserInfo>(userMap) else null
        }

        fun update(userInfo: UserInfo) {
            val key = Cache.key(userInfo.uid)
            Redis write {
                val pip = it.pipelined()
                Json.reflect(userInfo) { obj, name, field -> pip.hset(key, name, "${field.get(obj)}") }
                pip.sync()
            }
        }

        fun drop(uid: Long) {
            val key = Cache.key(uid)
            Redis write {
                val pip = it.pipelined()
                Json.reflect<UserInfo> { name, _ -> pip.hdel(key, name) }
                pip.sync()
            }
        }
    }

    interface UserInfoMapper {

        @Select("""
        SELECT * FROM user_info
        WHERE uid = #{uid}
        LIMIT 1
        """)
        fun getById(uid: Long): UserInfo?

        @Select("""
        SELECT * FROM user_info
        WHERE username = #{username}
        LIMIT 1
        """)
        fun getByName(username: String): UserInfo?

        @Select("""
        SELECT * FROM user_info
        WHERE email= #{email}
        LIMIT 1
        """)
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
    }
}
