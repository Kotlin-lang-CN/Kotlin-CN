package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.service.domain.Article
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.mysql.get

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleDao {

    init {
        Mysql.register(ArticleMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long, cache: Boolean): Article? {
        if (cache) {
            val cached = Cache.getById(id)
            if (cached != null) return cached
            val result = session[ArticleMapper::class].queryById(id)
            if (result != null && cache) Cache.update(result)
            return result
        } else {
            return session[ArticleMapper::class].queryById(id)
        }
    }

    fun getLatest(session: SqlSession, args: HashMap<String, String>, updateCache: Boolean = false): List<Article> {
        val result = session[ArticleMapper::class].queryLatest(args)
        if (updateCache) result.forEach { Cache.update(it) }
        return result
    }

    fun getByAuthor(session: SqlSession, id: Long, cache: Boolean): List<Article> {
        val result = session[ArticleMapper::class].queryByAuthor(id)
        if (cache) result.forEach { Cache.update(it) }
        return result
    }

    fun countByAuthor(session: SqlSession, id: Long): Int {
        return session[ArticleMapper::class].queryCountByAuthor(id)
    }

    fun createOrUpdate(session: SqlSession, article: Article) {
        val mapper = session[ArticleMapper::class]
        if (mapper.queryById(article.id) == null) {
            mapper.insert(article)
        } else {
            Cache.invalid(article.id)
            mapper.update(article)
        }
    }

    fun update(session: SqlSession, id: Long, args: HashMap<String, Any>) {
        val mapper = session[ArticleMapper::class]
        Cache.invalid(id)
        mapper.updateByArgs(args.apply { this["id"] = id })
    }

    private object Cache {

        fun key(id: Long) = "article:$id"

        fun getById(aid: Long): Article? {
            val userMap = Redis { it.hgetAll(key(aid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<Article>(userMap) else null
        }

        fun update(article: Article) {
            val key = Cache.key(article.id)
            Redis {
                val map = HashMap<String, String>()
                Json.reflect(article) { obj, name, field -> map[name] = "${field.get(obj)}" }
                it.hmset(key, map)
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun invalid(aid: Long) {
            Redis { it.del(key(aid)) }
        }
    }

    @Suppress("unused")
    interface ArticleMapper {

        @Select("""
        SELECT * FROM article
        WHERE id = #{id}
        LIMIT 1
        """)
        @Results(Result(property = "createTime", column = "create_time"),
                 Result(property = "lastEdit", column = "last_edit_time"),
                 Result(property = "lastEditUID", column = "last_edit_uid"),
                 Result(property = "contentId", column = "content_id"))
        fun queryById(id: Long): Article?

        @SelectProvider(type = SqlGenerator::class, method = "queryLatest")
        @Results(Result(property = "createTime", column = "create_time"),
                 Result(property = "lastEdit", column = "last_edit_time"),
                 Result(property = "lastEditUID", column = "last_edit_uid"),
                 Result(property = "contentId", column = "content_id"))
        fun queryLatest(args: HashMap<String, String>): List<Article>

        @Select("""
        SELECT * FROM article
        WHERE author = #{id}
        AND (state = ${Article.State.NORMAL} OR state = ${Article.State.FINE})
        ORDER BY create_time DESC
        """)
        @Results(Result(property = "createTime", column = "create_time"),
                 Result(property = "lastEdit", column = "last_edit_time"),
                 Result(property = "lastEditUID", column = "last_edit_uid"),
                 Result(property = "contentId", column = "content_id"))
        fun queryByAuthor(id: Long): List<Article>


        @Select("""
        SELECT COUNT(*) FROM article
        WHERE author = #{id}
        AND (state = ${Article.State.NORMAL} OR state = ${Article.State.FINE})
        """)
        fun queryCountByAuthor(id: Long): Int

        @Insert("""
        INSERT INTO article
        VALUES
        (#{id}, #{title}, #{author}, #{createTime}, #{category}, #{tags},
        #{lastEdit}, #{lastEditUID}, #{state}, #{contentId})
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
        state = #{state},
        content_id = #{contentId}
        WHERE
        id = #{id}
        """)
        fun update(article: Article)

        @UpdateProvider(type = SqlGenerator::class, method = "updateByArgs")
        fun updateByArgs(args: HashMap<String, Any>)

        class SqlGenerator {

            //更新文章内容
            fun updateByArgs(args: HashMap<String, Any>): String {
                return """
                UPDATE article SET
                 ${StringBuilder().apply {
                    args.forEach { k, _ -> append("$k = #{$k}, ") }
                    setLength(length - ", ".length)
                }}
                WHERE id = #{id}
                """
            }

            //查询文章内容
            fun queryLatest(args: HashMap<String, String>): String {
                val whereCause = when {
                    args.isEmpty() -> ""
                    else -> {
                        val sb = StringBuilder("WHERE")
                        args.forEach { k, v ->
                            if (k == "state" || k == "category") {
                                sb.append(" (")
                                v.split(",").forEach {
                                    sb.append("$k = $it OR ")
                                }
                                sb.setLength(sb.length - "OR ".length)
                                sb.append(") AND ")
                            } else {
                                sb.append("$k = #{k} AND ")
                            }
                        }
                        sb.setLength(sb.length - "AND ".length)
                        sb.toString()
                    }
                }
                return """
                SELECT * from article
                $whereCause
                ORDER BY create_time DESC
                """
            }
        }
    }

}