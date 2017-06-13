package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.service.domain.Reply
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.get
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyDao {

    init {
        Mysql.register(ReplyMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long): Reply? {
        val cached = ReplyCache.getById(id)
        if (cached != null) return cached
        val result = session[ReplyMapper::class].queryById(id)
        if (result != null) ReplyCache.update(result)
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

    internal object ReplyCache {

        fun key(id: Long) = "reply:$id"

        fun getById(uid: Long): Reply? {
            val userMap = Redis read { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<Reply>(userMap) else null
        }

        fun update(reply: Reply) {
            val key = ReplyCache.key(reply.id)
            Redis write {
                Json.reflect(reply) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 24 * 60 * 60)//cache for 24 hours
            }
        }

        fun invalid(aid: Long) {
            val key = ReplyCache.key(aid)
            Redis write { Json.reflect<Reply> { name, _ -> it.hdel(key, name) } }
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
        """)
        @Results(
                Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId")
        )
        fun queryById(id: Long): Reply?

        @Select("""
        SELECT * FROM reply
        WHERE reply_pool_id = #{replyPoolId}
        ORDER BY create_time DESC
        """)
        @Results(
                Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId")
        )
        fun queryByPool(replyPoolId: String): List<Reply>

        @Select("""
        SELECT COUNT(id) FROM reply
        WHERE reply_pool_id = #{replyPoolId}
        """)
        fun queryCountByPool(replyPoolId: String): Int

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