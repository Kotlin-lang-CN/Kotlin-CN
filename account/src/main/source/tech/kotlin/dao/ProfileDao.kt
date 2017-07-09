package tech.kotlin.dao

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.mysql.get
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.service.domain.Profile

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ProfileDao {

    init {
        Mysql.register(ProfileMapper::class.java)
    }

    fun getById(session: SqlSession, uid: Long, useCache: Boolean): Profile? {
        if (useCache) {
            val cached = Cache.getById(uid)
            if (cached != null) return cached
            val result = session[ProfileMapper::class].getById(uid)
            if (result != null && useCache) Cache.update(result)
            return result
        } else {
            return session[ProfileMapper::class].getById(uid)
        }
    }

    fun createOrUpdate(session: SqlSession, profile: Profile) {
        val mapper = session[ProfileMapper::class]
        val mayBeNull = mapper.getById(profile.uid)
        if (mayBeNull == null) {
            mapper.insert(profile)
        } else {
            mapper.update(profile)
            Cache.drop(profile.uid)
        }
    }

    private object Cache {

        fun key(uid: Long) = "profile:$uid"

        fun getById(uid: Long): Profile? {
            val profileMap = Redis { it.hgetAll(key(uid)) }
            return if (!profileMap.isEmpty()) Json.rawConvert<Profile>(profileMap) else null
        }

        fun update(profile: Profile) {
            val key = key(profile.uid)
            Redis {
                val map = HashMap<String, String>()
                Json.reflect(profile) { obj, name, field -> map[name] = "${field.get(obj)}" }
                it.hmset(key, map)
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun drop(uid: Long) {
            Redis { it.del(key(uid)) }
        }
    }

    interface ProfileMapper {

        @Select("""
        SELECT * FROM profile
        WHERE uid = #{uid}
        LIMIT 1
        """)
        fun getById(uid: Long): Profile?

        @Insert("""
        INSERT INTO profile VALUES
        (#{uid}, #{gender}, #{github}, #{blog}, #{company}, #{location}, #{description}, #{education})
        """)
        fun insert(profile: Profile)

        @Update("""
        UPDATE profile SET
        gender = #{gender},
        github = #{github},
        blog = #{blog},
        company = #{company},
        location = #{location},
        description = #{description},
        education = #{education}
        WHERE
        uid = #{uid}
        """)
        fun update(profile: Profile)
    }

}