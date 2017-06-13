package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.service.domain.Article
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.get
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleDao {

    init {
        Mysql.register(ArticleMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long, useCache: Boolean = false, updateCache: Boolean = false): Article? {
        if (useCache) {
            val cached = Cache.getById(id)
            if (cached != null) return cached
            val result = session[ArticleMapper::class].queryById(id)
            if (result != null && updateCache) Cache.update(result)
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

    fun createOrUpdate(session: SqlSession, article: Article) {
        val mapper = session[ArticleMapper::class]
        if (mapper.queryById(article.id) == null) {
            mapper.insert(article)
        } else {
            Cache.drop(article.id)
            mapper.update(article)
        }
    }

    fun update(session: SqlSession, id: Long, args: HashMap<String, Any>) {
        val mapper = session[ArticleMapper::class]
        Cache.drop(id)
        mapper.updateByArgs(args.apply { this["id"] = id })
    }

    internal object Cache {

        fun key(id: Long) = "article:$id"

        fun getById(aid: Long): Article? {
            val userMap = Redis read { it.hgetAll(key(aid)) }
            return if (!userMap.isEmpty()) Json.rawConvert<Article>(userMap) else null
        }

        fun update(article: Article) {
            val key = Cache.key(article.id)
            Redis write {
                Json.reflect(article) { obj, name, field -> it.hset(key, name, "${field.get(obj)}") }
                it.expire(key, 2 * 60 * 60)//cache for 2 hours
            }
        }

        fun drop(aid: Long) {
            val key = Cache.key(aid)
            Redis write { Json.reflect<Article> { name, _ -> it.hdel(key, name) } }
        }
    }

    @Suppress("unused")
    interface ArticleMapper {

        @Select("""
        SELECT * FROM article
        WHERE id = #{id}
        LIMIT 1
        """)
        @Results(
                Result(property = "createTime", column = "create_time"),
                Result(property = "lastEdit", column = "last_edit_time"),
                Result(property = "lastEditUID", column = "last_edit_uid"),
                Result(property = "contentId", column = "content_id")
        )
        fun queryById(id: Long): Article?

        @SelectProvider(type = SqlGenerator::class, method = "queryLatest")
        @Results(
                Result(property = "createTime", column = "create_time"),
                Result(property = "lastEdit", column = "last_edit_time"),
                Result(property = "lastEditUID", column = "last_edit_uid"),
                Result(property = "contentId", column = "content_id")
        )
        fun queryLatest(args: HashMap<String, String>): List<Article>

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