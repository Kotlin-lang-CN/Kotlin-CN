package tech.kotlin.dao.article

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import tech.kotlin.dao.account.AccountDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.TextContent
import tech.kotlin.utils.redis.Redis
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TextDao {

    fun getById(session: SqlSession, id: Long, useCache: Boolean = false, updateCache: Boolean = false): TextContent? {
        if (useCache) {
            val cached = Cache.getById(id)
            if (cached != null) return cached
            val result = session.getMapper(TextMapper::class.java).queryById(id)
            if (result != null && updateCache) Cache.update(result)
            return result
        } else {
            return session.getMapper(TextMapper::class.java).queryById(id)
        }
    }

    fun create(session: SqlSession, content: TextContent) {
        val mapper = session.getMapper(TextMapper::class.java)
        mapper.insert(content)
    }

    internal object Cache {

        fun key(id: Long) = "text_content:$id"

        fun getById(uid: Long): TextContent? {
            val userMap = Redis { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<TextContent>(userMap) else null
        }

        fun update(content: TextContent) {
            val key = Cache.key(content.id)
            Redis {
                val pip = it.pipelined()
                Json.reflect(content) { obj, name, field -> pip.hset(key, name, "${field.get(obj)}") }
                pip.expire(key, 2 * 60 * 60)//cache for 2 hours
                pip.sync()
            }
        }

        fun drop(uid: Long) {
            val key = key(uid)
            Redis {
                val pip = it.pipelined()
                Json.reflect<TextContent> { name, _ -> pip.hdel(key, name) }
                pip.sync()
            }
        }
    }

    interface TextMapper {

        @Insert("""
        INSERT INTO text_content
        VALUES
        (#{id}, #{content}, #{type}, #{createTime}, #{aliasId})
        """)
        fun insert(content: TextContent)


        @Select("""
        SELECT * FROM text_content
        WHERE id = #{id}
        """)
        fun queryById(id: Long): TextContent?

    }

}