/***
 * Mysql Database Mapping Logic
 * Library support by Mybatis3: compile 'org.mybatis:mybatis:3.4.0'
 * With plugin Mybatis Page helper: compile 'com.github.pagehelper:pagehelper:4.1.6'
 */
package tech.kotlin.china.mapper

import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import tech.kotlin.china.model.Account
import tech.kotlin.china.model.Article
import tech.kotlin.china.model.Comment
import tech.kotlin.china.model.Message

interface AccountMapper {
    @Select("""
    SELECT * FROM accounts
    WHERE uid = #{uid}
    """)
    fun queryByUID(uid: Long): Account?

    @Select("""
    SELECT * FROM accounts
    WHERE name = #{name}
    """)
    fun queryByName(name: String): Account?

    @Insert("""
    INSERT INTO accounts
    (name, password)
    VALUES
    (#{name}, #{password})
    """)
    fun registerAccount(data: Map<String, Any?>)

    @Select("""
    SELECT rank FROM accounts
    WHERE uid = #{id}
    """)
    fun seekRankOf(uid: Long): Int

    @Select("""
    SELECT * FROM accounts
    ORDER BY uid DESC
    """)
    fun queryUserList(): List<Account>

    @Select("""
    SELECT * FROM accounts
    WHERE rank = 1
    ORDER BY uid DESC
    """)
    fun queryAdminList(): List<Account>

    @Select("""
    SELECT * FROM accounts
    WHERE forbidden = true
    ORDER BY uid DESC
    """)
    fun queryDisabledList(): List<Account>

    @Update("""
    UPDATE accounts SET
    forbidden = #{forbidden}
    WHERE uid = #{uid}
    """)
    fun enableAccount(data: Map<String, Any?>)

    @Select("""
    SELECT COUNT(*) FROM accounts
    """)
    fun getUserCount(): Int
}

interface ArticleMapper {
    @Select("""
    SELECT * FROM articles
    WHERE aid = #{aid}
    """)
    fun queryByAID(aid: Long): Article?

    @Select("""
    SELECT * FROM articles
    WHERE forbidden = false
    ORDER BY aid DESC
    """)
    fun queryArticleList(): List<Article>

    @Select("""
    SELECT * FROM articles
    WHERE author = #{uid}
    ORDER BY aid DESC
    """)
    fun queryByUID(uid: Long): List<Article>

    @Insert("""
    INSERT INTO articles
    (title, description, content, author)
    VALUES
    (#{title}, #{description}, #{content}, #{uid})
    """)
    fun publishArticle(data: Map<String, Any?>)

    @Update("""
    UPDATE articles SET
    title = #{title},
    description = #{description},
    content = #{content}
    WHERE aid = #{aid}
    """)
    fun updateArticle(data: Map<String, Any?>)

    @Update("""
    UPDATE articles SET
    forbidden = #{disable}
    WHERE aid = #{aid}
    """)
    fun enableArticle(data: Map<String, Any?>)

    @Select("""
    SELECT COUNT(*) FROM articles
    """)
    fun getArticleCount(): Int

    @Update("""
    UPDATE articles SET
    `comment` = #{comment}
    WHERE aid = #{aid}
    """)
    fun updateCommentCount(data: Map<String, Any?>)

    @Update("""
    UPDATE articles SET
    `flower` = #{flower}
    WHERE aid = #{aid}
    """)
    fun updateFlowerCount(data: Map<String, Any?>)
}

interface CommentMapper {
    @Select("""
    SELECT * FROM comments
    WHERE cid = #{cid}
    """)
    fun queryByCID(cid: Long): Comment?

    @Select("""
    SELECT * FROM comments
    WHERE aid = #{aid} AND forbidden = FALSE AND `delete` = FALSE
    ORDER BY cid DESC
    """)
    fun queryByAID(aid: Long): List<Comment>

    @Select("""
    SELECT * FROM comments
    WHERE commenter = #{uid} AND `delete` = FALSE
    ORDER BY cid DESC
    """)
    fun queryByUID(uid: Long): List<Comment>

    @Insert("""
    INSERT INTO comments
    (aid, commenter, content, reply)
    VALUES
    (#{aid}, #{uid}, #{content}, #{reply})
    """)
    fun makeComment(data: Map<String, Any?>)

    @Update("""
    UPDATE comments SET
    `delete` = TRUE
    WHERE cid = #{cid}
    """)
    fun deleteComment(cid: Long)

    @Update("""
    UPDATE comments SET
    forbidden = #{disable}
    WHERE cid = #{cid}
    """)
    fun enableComment(data: Map<String, Any?>)

    @Select("""
    SELECT COUNT(*) FROM comments
    WHERE aid = #{aid} AND forbidden = FALSE AND `delete` = FALSE
    """)
    fun countComment(aid: Long): Long

    @Update("""
    UPDATE comments SET
    `flower` = #{flower}
    WHERE cid = #{cid}
    """)
    fun updateFlowerCount(data: Map<String, Any?>)
}

interface FlowerMapper {
    @Insert("""
    INSERT INTO flowers
    (`mode`, oid, actor)
    VALUES
    (#{mode}, #{oid}, #{actor})
    """)
    fun addFlower(data: Map<String, Any?>)

    @Delete("""
    DELETE FROM flowers
    WHERE `mode` = #{mode} AND oid = #{oid} AND actor = #{actor}
    """)
    fun cancelFlower(data: Map<String, Any?>)

    @Select("""
    SELECT COUNT(*) FROM flowers
    WHERE `mode` = #{mode} AND oid = #{oid} AND actor = #{actor}
    """)
    fun queryActor(data: Map<String, Any?>): Int

    @Select("""
    SELECT COUNT(*) FROM flowers
    WHERE `mode` = #{mode} AND oid = #{oid}
    """)
    fun objectPraisedCount(data: Map<String, Any?>): Int
}

interface MessageMapper {
    @Insert("""
    INSERT INTO messages
    (content, title, `from`, `to`)
    VALUES
    (#{content}, #{title}, #{from}, #{to})
    """)
    fun addMessage(data: Map<String, Any?>)

    @Select("""
    SELECT * FROM messages
    WHERE `to` = #{uid} AND status = #{status}
    ORDER BY id DESC
    """)
    fun getMyMessage(data: Map<String, Any?>): List<Message>

    @Select("""
    SELECT * FROM messages
    WHERE `to` = #{uid}
    ORDER BY id DESC
    """)
    fun getAllMyMessage(uid: Long): List<Message>

    @Select("""
    SELECT COUNT(*) FROM messages
    WHERE `to` = #{uid} AND status = #{status}
    """)
    fun countMyMessage(data: Map<String, Any?>): Int

    @Select("""
    SELECT COUNT(*) FROM messages
    WHERE `to` = #{uid}
    """)
    fun countAllMyMessage(uid: Long): Int

    @Select("""
    SELECT * FROM messages
    WHERE id = #{id}
    """)
    fun queryById(id: Long): Message?

    @Update("""
    UPDATE messages SET
    status = 1
    WHERE `to` = #{uid}
    """)
    fun readAllMessages(uid: Long)

    @Update("""
    UPDATE messages SET
    status = 1
    WHERE id = #{id}
    """)
    fun readMessage(id: Long)
}