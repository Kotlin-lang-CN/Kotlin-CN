/***
 * Mysql Database Mapping Logic
 * Library support by Mybatis3: compile 'org.mybatis:mybatis:3.4.0'
 * With plugin Mybatis Page helper: compile 'com.github.pagehelper:pagehelper:4.1.6'
 */
package tech.kotlin.china.database

import com.github.pagehelper.PageHelper
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.apache.ibatis.session.SqlSession
import utils.map.Row

fun SqlSession.page(page: Int, size: Int = 20) = PageHelper.startPage<Row>(page, size)

interface AccountMapper {
    @Select("""
    SELECT * FROM accounts
    WHERE id = #{id}
    """) fun queryById(id: Long): Row?

    @Insert("""
    INSERT INTO accounts
    (id, name, token, avatar_url, html_url, email)
    VALUES
    (#{id}, #{name}, #{token}, #{avatar_url}, #{html_url}, #{email})
    """) fun addAccount(row: Row)

    @Update("""
    UPDATE accounts SET
    name = #{name},
    token = #{token},
    avatar_url = #{avatar_url},
    html_url = #{html_url},
    email = #{email}
    WHERE id = #{id}
    """) fun updateAccount(row: Row)
}

interface ArticleMapper {
    @Select("""
    SELECT
    a.*, ac.name, ac.avatar_url, ac.html_url, ac.email,
    COUNT(c.cid) as comment, COUNT(f.id) as flower
    FROM articles as a
    LEFT JOIN accounts as ac
    ON a.author = ac.id
    LEFT JOIN comments as c
    ON a.aid = c.aid
    LEFT JOIN flowers as f
    ON a.aid = f.oid AND f.mode = 0
    WHERE a.aid = #{aid}
    GROUP BY a.aid
    """) fun queryByAID(aid: Long): Row?

    @Select("""
    SELECT
    a.aid, a.author, a.title, a.category, a.create_time,
    ac.name, ac.avatar_url, ac.html_url, ac.email,
    COUNT(c.cid) as comment, COUNT(f.id) as flower
    FROM articles as a
    LEFT JOIN accounts as ac
    ON a.author = ac.id
    LEFT JOIN comments as c
    ON a.aid = c.aid
    LEFT JOIN flowers as f
    ON a.aid = f.oid AND f.mode = 0
    GROUP BY a.aid
    ORDER BY a.create_time DESC
    """) fun articleList(): List<Row>

    @Select("""
    SELECT
    a.aid, a.author, a.title, a.category, a.create_time,
    ac.name, ac.avatar_url, ac.html_url, ac.email,
    COUNT(c.cid) as comment, COUNT(f.id) as flower
    FROM articles as a
    LEFT JOIN accounts as ac
    ON a.author = ac.id
    LEFT JOIN comments as c
    ON a.aid = c.aid
    LEFT JOIN flowers as f
    ON a.aid = f.oid AND f.mode = 0
    WHERE a.category = #{category}
    GROUP BY a.aid
    ORDER BY a.create_time DESC
    """) fun articleListWithCategory(category: String): List<Row>

    @Select("""
    SELECT
    a.aid, a.author, a.title, a.category, a.create_time,
    ac.name, ac.avatar_url, ac.html_url, ac.email,
    COUNT(c.cid) as comment, COUNT(f.id) as flower
    FROM articles as a
    LEFT JOIN accounts as ac
    ON a.author = ac.id
    LEFT JOIN comments as c
    ON a.aid = c.aid
    LEFT JOIN flowers as f
    ON a.aid = f.oid AND f.mode = 0
    WHERE a.author = #{author}
    GROUP BY a.aid
    ORDER BY a.create_time DESC
    """) fun myArticleList(author: Long): List<Row>

    @Select("""
    SELECT
    a.aid, a.author, a.title, a.category, a.create_time,
    ac.name, ac.avatar_url, ac.html_url, ac.email,
    COUNT(c.cid) as comment, COUNT(f.id) as flower
    FROM articles as a
    LEFT JOIN accounts as ac
    ON a.author = ac.id
    LEFT JOIN comments as c
    ON a.aid = c.aid
    LEFT JOIN flowers as f
    ON a.aid = f.oid AND f.mode = 0
    WHERE a.category = #{category} AND a.author = #{author}
    GROUP BY a.aid
    ORDER BY a.create_time DESC
    """) fun myArticleListWithCategory(row: Row): List<Row>

    @Insert("""
    INSERT INTO articles
    (author, title, content, category)
    VALUES
    (#{author}, #{title}, #{content}, #{category})
    """) fun publishArticle(row: Row)

}

interface CommentMapper {
    @Insert("""
    INSERT INTO comments
    (aid, commenter, reply, content)
    VALUES
    (#{aid}, #{commenter}, #{reply}, #{content})
    """) fun makeComment(row: Row)

    @Select("""
    SELECT
    c.cid, c.commenter, c.reply, c.content, c.create_time
    FROM comments as c
    WHERE aid = #{aid}
    ORDER BY create_time DESC
    """) fun queryByAID(aid: Long): List<Row>

    @Select("""
    SELECT * FROM comments
    ORDER BY create_time DESC
    """) fun queryMyComments(commenter: Long): List<Row>

    @Select("""
    SELECT *
    FROM comments
    WHERE comments.aid in (SELECT articles.aid FROM articles WHERE author = #{id})
    UNION
    SELECT *
    FROM comments
    WHERE reply = #{id}
    ORDER BY create_time DESC
    """) fun queryMyReply(id: Long): List<Row>

    @Select("""
    SELECT * FROM comments
    WHERE cid = #{cid}
    """) fun queryByCID(cid: Long): Row?
}

interface FlowerMapper {
    @Insert("""
    INSERT INTO flowers
    (mode, oid, actor, praised)
    VALUES
    (#{mode}, #{oid}, #{actor}, #{praised})
    """) fun flower(row: Row)

    @Delete("""
    DELETE FROM flowers
    WHERE mode = #{mode} AND oid = #{oid} AND actor = #{actor}
    """) fun cancelFlower(row: Row)

    @Select("""
    SELECT COUNT(*) FROM flowers
    WHERE oid = #{oid} AND mode = #{mode}
    """) fun countFlower(row: Row): Int

    @Select("""
    SELECT COUNT(*) FROM  flowers
    WHERE praised = #{id}
    """) fun countBePraised(id: Long): Int

    @Select("""
    SELECT * FROM flowers
    WHERE mode = #{mode} AND oid = #{oid} AND actor = #{actor}
    """) fun queryAction(row: Row): Row?
}