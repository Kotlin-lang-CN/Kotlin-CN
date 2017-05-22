package tech.kotlin.article.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.model.Article
import tech.kotlin.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleDao {

    init {
        Mysql.register(ArticleMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long): Article? {
        return session.getMapper(ArticleMapper::class.java).queryById(id)
    }

    fun createOrUpdate(session: SqlSession, article: Article) {
        val mapper = session.getMapper(ArticleMapper::class.java)
        if (mapper.queryById(article.id) == null) {
            mapper.insert(article)
        } else {
            mapper.update(article)
        }
    }

    fun update(session: SqlSession, id: Long, args: HashMap<String, Any>) {
        val mapper = session.getMapper(ArticleMapper::class.java)
        mapper.updateByArgs(args.apply { this["id"] = id })
    }

    interface ArticleMapper {

        @Select("""
        SELECT * FROM article
        WHERE id = #{id}
        LIMIT 1
        """)
        @Results(
                Result(property = "createTime", column = "create_time"),
                Result(property = "lastEdit", column = "last_edit_time"),
                Result(property = "lastEditUID", column = "last_edit_uid")
        )
        fun queryById(id: Long): Article?

        @Insert("""
        INSERT INTO article
        VALUES
        (#{id}, #{title}, #{author}, #{createTime}, #{category}, #{tags},
         #{lastEdit}, #{lastEditUID}, #{state})
        """)
        fun insert(article: Article)

        @Update("""
        UPDATE article set
        title = #{title},
        author = #{author},
        create_time = #{createTime},
        category = #{category},
        tags = #{tags},
        last_edit_time = #{lastEdit},
        last_edit_uid = #{lastEditUID},
        state = #{state}
        WHERE
        id = #{id}
        """)
        fun update(article: Article)

        @UpdateProvider(type = SqlGenerator::class, method = "updateByArgs")
        fun updateByArgs(args: HashMap<String, Any>)

        class SqlGenerator {

            fun updateByArgs(args: HashMap<String, Any>): String {
                return """
                UPDATE article SET
                 ${StringBuilder().apply {
                    args.forEach { k, _ -> append("$k = #{$k} ,") }
                    setLength(length - ",".length)
                }}
                WHERE id = #{id}
                """
            }
        }
    }

}