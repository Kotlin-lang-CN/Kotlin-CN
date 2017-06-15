package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.service.domain.GithubUser
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.get
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubUserInfoDao {

    init {
        Mysql.register(GithubUserInfoMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long, useCache: Boolean = false, updateCache: Boolean = false): GithubUser? {
        if (useCache) {
            val cached = Cache.getById(id)
            if (cached != null) return cached
            val result = session[GithubUserInfoMapper::class].getById(id)
            if (result != null && updateCache) Cache.update(result)
            return result
        } else {
            return session[GithubUserInfoMapper::class].getById(id)
        }
    }

    fun saveOrUpdate(session: SqlSession, user: GithubUser) {
        val mapper = session[GithubUserInfoMapper::class]
        val mayBeNull = mapper.getById(user.id)
        if (mayBeNull == null) {
            mapper.insert(user)
        } else {
            Cache.drop(user.id)
            mapper.update(user)
        }
    }

    internal object Cache {

        fun key(id: Long) = "github_user_info:$id"

        fun getById(id: Long): GithubUser? {
            val userMap = Redis read { it.hgetAll(key(id)) }
            return if (!userMap.isEmpty()) Json.rawConvert<GithubUser>(userMap) else null
        }

        fun update(githubUser: GithubUser) {
            val key = Cache.key(githubUser.id)
            Redis write {
                Json.reflect(githubUser) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun drop(id: Long) {
            val key = Cache.key(id)
            Redis write { Json.reflect<GithubUser> { name, _ -> it.hdel(key, name) } }
        }
    }

    interface GithubUserInfoMapper {

        @Update("""
        UPDATE github_user_info SET
        access_token = #{accessToken},
        name = #{name},
        email = #{email},
        avatar = #{avatar},
        login = #{login},
        blog = #{blog},
        location = #{location},
        follower_count = #{followerCount},
        company = #{company}
        WHERE
        id = #{id}
        """)
        fun update(githubUser: GithubUser)

        @Insert("""
        INSERT INTO github_user_info
        VALUES
        (#{uid}, #{accessToken}, #{id}, #{name}, #{email}, #{avatar}, #{login}, #{blog},
        #{location}, #{followerCount}, #{company})
        """)
        fun insert(githubUser: GithubUser)

        @Select("""
        SELECT * FROM github_user_info
        WHERE id = #{id}
        """)
        @Results(
                Result(column = "access_token", property = "accessToken"),
                Result(column = "follower_count", property = "followerCount")
        )
        fun getById(id: Long): GithubUser?
    }

}