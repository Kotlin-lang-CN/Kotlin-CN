package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.redis.Redis
import tech.kotlin.service.domain.Reply
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.mysql.get
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyDao {

    init {
        Mysql.register(ReplyMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long, cache: Boolean): Reply? {
        if (cache) {
            val cached = ReplyCache.getById(id)
            if (cached != null) return cached
        }
        val result = session[ReplyMapper::class].queryById(id)
        if (result != null && cache) {
            ReplyCache.update(result)
        }
        return result
    }

    fun create(session: SqlSession, reply: Reply) {
        val mapper = session[ReplyMapper::class]
        mapper.insert(reply)
    }

    fun update(session: SqlSession, id: Long, args: HashMap<String, String>) {
        val mapper = session[ReplyMapper::class]
        ReplyCache.invalid(id)
        mapper.updateByArgs(args.apply { this["id"] = "$id" })
    }

    fun getByPool(session: SqlSession, replyPoolId: String): List<Reply> {
        val result = session[ReplyMapper::class].queryByPool(replyPoolId)
        result.forEach { ReplyCache.update(it) }
        return result
    }

    fun getCountByPoolId(session: SqlSession, replyPoolId: String): Int {
        if (!replyPoolId.isNullOrBlank()) {
            return session[ReplyMapper::class].queryCountByPool(replyPoolId)
        } else {
            return session[ReplyMapper::class].queryCount()
        }
    }

    fun getByAuthor(session: SqlSession, author: Long): List<Reply> {
        val result = session[ReplyMapper::class].queryByAuthor(author)
        result.forEach { ReplyCache.update(it) }
        return result
    }

    fun getCountByAuthor(session: SqlSession, author: Long): Int {
        return session[ReplyMapper::class].queryCountByAuthor(author)
    }

    private object ReplyCache {

        fun key(id: Long) = "reply:$id"

        fun getById(aid: Long): Reply? {
            val replyMap = Redis { it.hgetAll(key(aid)) }
            return if (!replyMap.isEmpty()) Json.rawConvert<Reply>(replyMap) else null
        }

        fun update(reply: Reply) {
            val key = ReplyCache.key(reply.id)
            Redis {
                val map = HashMap<String, String>()
                Json.reflect(reply) { obj, name, field -> map[name] = "${field.get(obj)}" }
                it.hmset(key, map)
                it.expire(key, 24 * 60 * 60)//cache for 24 hours
            }
        }

        fun invalid(aid: Long) {
            Redis { it.del(key(aid)) }
        }
    }

    interface ReplyMapper {

        @Insert("""
        INSERT INTO reply
        VALUES
        (#{id}, #{replyPoolId}, #{ownerUID}, #{createTime}, #{state}, #{contentId}, #{aliasId})
        """)
        fun insert(content: Reply)

        @Select("""
        SELECT * FROM reply
        WHERE id = #{id}
        LIMIT 1
        """)
        @Results(Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId"))
        fun queryById(id: Long): Reply?

        @Select("""
        SELECT * FROM reply
        WHERE reply_pool_id = #{replyPoolId}
        ORDER BY create_time
        """)
        @Results(Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId"))
        fun queryByPool(replyPoolId: String): List<Reply>

        @Select("""
        SELECT COUNT(id) FROM reply
        WHERE reply_pool_id = #{replyPoolId}
        """)
        fun queryCountByPool(replyPoolId: String): Int

        @Select("""
        SELECT * FROM reply
        WHERE owner_uid = #{author}
        AND state = ${Reply.State.NORMAL}
        ORDER by create_time DESC
        """)
        @Results(Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId"))
        fun queryByAuthor(author: Long): List<Reply>

        @Select("""
        SELECT COUNT(id) FROM reply
        WHERE owner_uid = #{author}
        AND state = ${Reply.State.NORMAL}
        """)
        fun queryCountByAuthor(author: Long): Int

        @Select("""
        SELECT COUNT(id) FROM reply
        """)
        fun queryCount(): Int

        @UpdateProvider(type = SqlGenerator::class, method = "updateByArgs")
        fun updateByArgs(args: HashMap<String, String>)

        class SqlGenerator {

            fun updateByArgs(args: HashMap<String, String>): String {
                return """
                UPDATE reply SET
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