package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.model.Reply
import org.apache.ibatis.annotations.*

@Mapper
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

    @UpdateProvider(type = SqlGenerator::class, method = "replyUpdateByArgs")
    fun updateByArgs(args: HashMap<String, String>)

    class SqlGenerator {

        fun replyUpdateByArgs(args: HashMap<String, String>): String {
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