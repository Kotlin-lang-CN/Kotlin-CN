package tech.kotlin.china.restful.database

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import tech.kotlin.china.restful.model.Account
import tech.kotlin.china.restful.model.Article
import tech.kotlin.china.restful.model.Comment

/***
 * Mysql Database Logic
 * Library support by Mybatis3: compile 'org.mybatis:mybatis:3.4.0'
 * With plugin Mybatis Page helper: compile 'com.github.pagehelper:pagehelper:4.1.6'
 */

operator fun <T> SqlSession.get(mapper: Class<T>): T = getMapper(mapper)

fun <T, R> SqlSession.use(mapper: Class<T>, action: (T) -> R): R = action(getMapper(mapper))

interface AccountMapper {
    @Select("SELECT * FROM accounts WHERE uid = #{uid}")
    fun queryByUID(uid: Long): Account?

    @Select("SELECT * FROM accounts WHERE name = #{name}")
    fun queryByName(name: String): Account?

    @Insert("INSERT INTO accounts (name, password) VALUES (#{name}, #{password})")
    fun registerAccount(data: Map<String, Any?>)

    @Select("SELECT rank FROM accounts WHERE uid = #{id}")
    fun seekRankOf(uid: Long): Int

    @Select("SELECT * FROM accounts ORDER BY uid DESC")
    fun queryUserList(): List<Account>

    @Select("SELECT * FROM accounts WHERE rank = 1 ORDER BY uid DESC")
    fun queryAdminList(): List<Account>

    @Select("SELECT * FROM accounts WHERE forbidden = true ORDER BY uid DESC")
    fun queryDisabledList(): List<Account>

    @Update("UPDATE accounts set forbidden = #{forbidden} WHERE uid = #{uid}")
    fun enableAccount(data: Map<String, Any?>)

    @Select("SELECT COUNT(*) FROM accounts")
    fun getUserCount(): Int
}

interface ArticleMapper {
    @Select("SELECT * FROM articles WHERE aid = #{aid}")
    fun queryByAID(aid: Long): Article?

    @Select("SELECT * FROM articles WHERE forbidden = false ORDER BY aid DESC")
    fun queryArticleList(): List<Article>

    @Select("SELECT * FROM articles WHERE author = #{uid} ORDER BY aid DESC")
    fun queryByUID(uid: Long): List<Article>

    @Insert("INSERT INTO articles (title, description, content, author) VALUES (#{title}, #{description}, #{content}, #{uid})")
    fun publishArticle(data: Map<String, Any?>)

    @Update("UPDATE articles set title = #{title},description = #{description},content = #{content} WHERE aid = #{aid}")
    fun updateArticle(data: Map<String, Any?>)

    @Update("UPDATE articles set forbidden = #{disable} WHERE aid = #{aid}")
    fun enableArticle(data: Map<String, Any?>)

    @Select("SELECT COUNT(*) FROM articles")
    fun getArticleCount(): Int
}

interface CommentMapper {
    @Select("SELECT * FROM comments WHERE cid = #{cid}")
    fun queryByCID(cid: Long): Comment?

    @Select("SELECT * FROM comments WHERE aid = #{aid} AND forbidden = FALSE AND `delete` = FALSE ORDER BY cid DESC")
    fun queryByAID(aid: Long): List<Comment>

    @Select("SELECT * FROM comments WHERE commenter = #{uid} AND `delete` = FALSE ORDER BY cid DESC")
    fun queryByUID(uid: Long): List<Comment>

    @Insert("INSERT INTO comments (aid, commenter, content, reply) VALUES (#{aid}, #{uid}, #{content}, #{reply})")
    fun makeComment(data: Map<String, Any?>)

    @Update("UPDATE comments set `delete` = TRUE WHERE cid = #{cid}")
    fun deleteComment(cid: Long)

    @Update("UPDATE comments set forbidden = #{disable} WHERE cid = #{cid}")
    fun enableComment(data: Map<String, Any?>)
}
