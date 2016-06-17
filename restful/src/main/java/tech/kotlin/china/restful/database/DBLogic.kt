package tech.kotlin.china.restful.database

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import tech.kotlin.china.restful.model.Account
import tech.kotlin.china.restful.model.Article

/***
 * Mysql Database Logic
 * Library support by Mybatis3: compile 'org.mybatis:mybatis:3.4.0'
 * With plugin Mybatis Page helper: compile 'com.github.pagehelper:pagehelper:4.1.6'
 */

operator fun <T> SqlSession.get(mapper: Class<T>): T = getMapper(mapper)

fun <T, R> SqlSession.use(mapper: Class<T>, action: (T) -> R): R = action(getMapper(mapper))

interface AccountMapper {
    @Select("SELECT * FROM accounts where uid = #{uid}")
    fun queryByUID(uid: Long): Account?

    @Select("SELECT * FROM accounts where name = #{name}")
    fun queryByName(name: String): Account?

    @Insert("INSERT INTO accounts (name, password) values(#{name}, #{password})")
    fun registerAccount(data: Map<String, Any>)

    @Select("SELECT rank FROM accounts where uid = #{id}")
    fun seekRankOf(uid: Long): Int

    @Select("SELECT * FROM accounts")
    fun queryUserList(): List<Account>

    @Select("SELECT * FROM accounts where rank = 1")
    fun queryAdminList(): List<Account>

    @Select("SELECT * FROM accounts where forbidden = true")
    fun queryDisabledList(): List<Account>

    @Update("UPDATE accounts set forbidden = #{forbidden} where uid = #{uid}")
    fun enableAccount(data: Map<String, Any>)

    @Select("SELECT COUNT(*) FROM accounts")
    fun getUserCount(): Int
}

interface ArticleMapper {
    @Select("SELECT * FROM articles where aid = #{aid}")
    fun queryByAID(aid: Long): Article?

    @Select("SELECT * FROM articles where forbidden = false")
    fun queryArticleList(): List<Article>

    @Select("SELECT * FROM articles where author = #{uid}")
    fun queryByUID(uid: Long): List<Article>

    @Insert("INSERT INTO articles (title, description, content, author) " +
            "values (#{title}, #{description}, #{content}, #{uid})")
    fun publishArticle(data: Map<String, Any>)

    @Update("UPDATE articles set title = #{title},description = #{description},content = #{content} where aid = #{aid}")
    fun updateArticle(data: Map<String, Any>)

    @Update("UPDATE articles set forbidden = #{disable} where aid = #{aid}")
    fun enableArticle(data: Map<String, Any>)

    @Select("SELECT COUNT(*) FROM articles")
    fun getArticleCount(): Int
}
