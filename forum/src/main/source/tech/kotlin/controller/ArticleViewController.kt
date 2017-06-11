package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.dict
import tech.kotlin.common.utils.str
import tech.kotlin.model.domain.Article
import tech.kotlin.model.domain.Category
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.QueryLatestArticleReq
import tech.kotlin.model.request.QueryTextReq
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.service.account.Users
import tech.kotlin.service.article.Articles
import tech.kotlin.service.article.Texts
import tech.kotlin.utils.Err
import tech.kotlin.utils.check
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.StringWriter
import javax.xml.transform.TransformerFactory


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleViewController {

    val properties = Props.loads("project.properties")
    val cgiHost = properties str "cgi.host"

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

    val getCategory = Route { _, _ -> ok { it["category"] = Category.values().map { it.value } } }

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
        val df = SimpleDateFormat("E, MM dd yyyy HH:mm:ss z", Locale.US)
        val localeDivide = 14 * 60 * 60 * 1000
        val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument().apply {
            appendChild(createElement("rss").apply {
                setAttribute("version", "2.0")
                appendChild(createElement("channel").apply {
                    appendChild(createElement("title").apply { textContent = "Kotlin-CN 中文网" })
                    appendChild(createElement("link").apply { textContent = cgiHost })
                    appendChild(createElement("description").apply { textContent = "社区最新精品文章" })
                    appendChild(createElement("language").apply { textContent = "${Locale.CHINA}" })
                    articles.forEach {
                        appendChild(createElement("item").apply {
                            appendChild(createElement("title").apply { textContent = it.title })
                            appendChild(createElement("description").apply {
                                textContent = contents[it.contentId]!!.content
                            })
                            appendChild(createElement("author").apply { textContent = users[it.author]!!.username })
                            appendChild(createElement("pubDate").apply {
                                textContent = df.format(it.createTime - localeDivide)
                            })
                            appendChild(createElement("link").apply { textContent = "$cgiHost/#/topic/${it.id}" })
                            appendChild(createElement("guid").apply { textContent = "$cgiHost/#/topic/${it.id}" })
                        })
                    }
                })
            })
        }
        resp.header("Content-Type", "application/xml")
        return@Route StringWriter().apply {
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(DOMSource(doc), StreamResult(this))
        }.toString()
    }
}