package tech.kotlin.dao.article

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.dao.account.AccountDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.TextContent
import tech.kotlin.utils.mysql.Mysql
import tech.kotlin.utils.mysql.get
import tech.kotlin.utils.redis.Redis
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TextDao {

    init {
        Mysql.register(TextMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long): TextContent? {
        val cached = Cache.getById(id)
        if (cached != null) return cached
        val result = session[TextMapper::class].queryById(id)
        if (result != null) Cache.update(result)
        return result
    }

    fun create(session: SqlSession, content: TextContent) {
        val mapper = session[TextMapper::class]
        mapper.insert(content)
    }

    internal object Cache {

        fun key(id: Long) = "text_content:$id"

        fun getById(uid: Long): TextContent? {
            val userMap = Redis read { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<TextContent>(userMap) else null
        }

        fun update(content: TextContent) {
            val key = Cache.key(content.id)
            Redis write {
                Json.reflect(content) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
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
        @Results(
                Result(column = "create_time", property = "createTime"),
                Result(column = "alias_id", property = "aliasId")
        )
        fun queryById(id: Long): TextContent?

    }

}