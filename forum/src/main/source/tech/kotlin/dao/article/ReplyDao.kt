package tech.kotlin.dao.article

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.model.domain.Reply
import tech.kotlin.utils.mysql.Mysql
import tech.kotlin.utils.mysql.get
import tech.kotlin.utils.redis.Redis
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyDao {

    init {
        Mysql.register(ReplyMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long): Reply? {
        val cached = Cache.getById(id)
        if (cached != null) return cached
        val result = session[ReplyMapper::class].queryById(id)
        if (result != null) Cache.update(result)
        return result
    }

    fun create(session: SqlSession, reply: Reply) {
        val mapper = session[ReplyMapper::class]
        mapper.insert(reply)
    }

    fun update(session: SqlSession, id: Long, args: HashMap<String, String>) {
        val mapper = session[ReplyMapper::class]
        Cache.drop(id)
        mapper.updateByArgs(args.apply { this["id"] = "$id" })
    }

    fun getByPool(session: SqlSession, replyPoolId: String): List<Reply> {
        val result = session[ReplyMapper::class].queryByPool(replyPoolId)
        result.forEach { Cache.update(it) }
        return result
    }

    internal object Cache {

        fun key(id: Long) = "reply:$id"

        fun getById(uid: Long): Reply? {
            val userMap = Redis read { it.hgetAll(key(uid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<Reply>(userMap) else null
        }

        fun update(reply: Reply) {
            val key = Cache.key(reply.id)
            Redis write {
                Json.reflect(reply) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 24 * 60 * 60)//cache for 24 hours
            }
        }

        fun drop(aid: Long) {
            val key = Cache.key(aid)
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
        WHERE reply_pool_id = #{pool}
        AND state = ${Reply.State.NORMAL}
        """)
        @Results(
                Result(column = "reply_pool_id", property = "replyPoolId"),
                Result(column = "owner_uid", property = "ownerUID"),
                Result(column = "create_time", property = "createTime"),
                Result(column = "content_id", property = "contentId"),
                Result(column = "alias_id", property = "aliasId")
        )
        fun queryByPool(pool: String): List<Reply>

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