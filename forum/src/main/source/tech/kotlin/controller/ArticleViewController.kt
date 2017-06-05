package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Article
import tech.kotlin.model.domain.Category
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.service.account.Users
import tech.kotlin.service.article.Articles
import tech.kotlin.model.request.QueryLatestArticleReq
import tech.kotlin.model.request.QueryTextReq
import tech.kotlin.service.article.Texts
import tech.kotlin.utils.Err
import tech.kotlin.utils.check
import tech.kotlin.common.utils.dict
import java.text.SimpleDateFormat
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleViewController {
    val getList = Route { req, _ ->
        val offset = req.queryParams("offset")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val limit = req.queryParams("limit")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 20

        val articles = Articles.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(Users.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        return@Route ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }

    val getByCategory = Route { req, _ ->
        val category = req.params(":id")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val offset = req.queryParams("offset")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val limit = req.queryParams("limit")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 20

        val articles = Articles.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.category = "$category"

        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(Users.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        return@Route ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }

    val getFine = Route { req, _ ->
        val offset = req.queryParams("offset")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val limit = req.queryParams("limit")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 20

        val articles = Articles.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.state = "${Article.State.FINE}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(Users.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        return@Route ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }

    val getCategory = Route { _, _ ->
        return@Route ok { it["category"] = Category.values().map { it.value } }
    }

    val rssFine = Route { _, resp ->
        val articles = Articles.getLatest(QueryLatestArticleReq().apply {
            this.offset = 0
            this.limit = 20
            this.state = "${Article.State.FINE}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(Users.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                }.distinctBy { it }
            }).info)
        }

        val contents = HashMap<Long, TextContent>()
        if (articles.isNotEmpty()) {
            contents.putAll(Texts.getById(QueryTextReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.contentId })
                }
            }).result)
        }

        resp.header("Content-Type", "application/xml")
        return@Route """
    <?xml version='1.0' encoding='UTF-8'?>
    <rss version="2.0">
      <channel>
        <title>Kotlin-CN 中文网</title>
        <link>http://www.baidu.com</link>
        <description>社区最新精品文章</description>
        <language>${Locale.CHINA}</language>
        ${{
            val df = SimpleDateFormat("E, MM dd yyyy HH:mm:ss z", Locale.US)
            val localeDivide = 14 * 60 * 60 * 1000
            var result = ""
            articles.forEach {
                result += """
                <item>
                    <title>${it.title}</title>
                    <description>${contents[it.contentId]!!.content}</description>
                    <author>${users[it.author]}</author>
                    <pubDate>${df.format(it.createTime - localeDivide)}</pubDate>
                    <link>http://www.baidu.com</link>
                    <guid>http://www.baidu.com/${it.id}</guid>
                </item>
                """.trimIndent()
            }
            result
        }()}
      </channel>
    </rss>
    """.trimIndent().trim()
    }
}