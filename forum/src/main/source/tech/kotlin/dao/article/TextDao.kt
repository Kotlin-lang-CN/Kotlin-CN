package tech.kotlin.dao.article

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.session.SqlSession
import tech.kotlin.model.domain.TextContent
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.get
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json

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
                it.expire(key, 24 * 60 * 60)//cache for 24 hours
            }
        }

    }

    interface TextMapper {

        @Insert("""
        INSERT INTO text_content
        VALUES
        (#{id}, #{content}, #{serializeId}, #{createTime})
        """)
        fun insert(content: TextContent)

        @Select("""
        SELECT * FROM text_content
        WHERE id = #{id}
        """)
        @Results(
                Result(column = "serialize_id", property = "serializeId"),
                Result(column = "create_time", property = "createTime")
        )
        fun queryById(id: Long): TextContent?

    }

}