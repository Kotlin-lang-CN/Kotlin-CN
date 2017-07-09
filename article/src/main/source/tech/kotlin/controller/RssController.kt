package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.str
import tech.kotlin.service.ArticleService
import tech.kotlin.service.domain.Article
import tech.kotlin.service.domain.TextContent
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.article.req.QueryLatestArticleReq
import tech.kotlin.service.article.req.QueryTextReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.ServDef
import tech.kotlin.service.TextService
import tech.kotlin.service.account.UserApi
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object RssController {

    val frontendHost = Props str "deploy.frontend.host"

    val userApi by Serv.bind(UserApi::class, ServDef.ACCOUNT)

    val fine = Route { _, resp ->
        val articles = ArticleService.getLatest(QueryLatestArticleReq().apply {
            this.offset = 0
            this.limit = 20
            this.state = "${Article.State.FINE}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                }.distinctBy { it }
            }).info)
        }

        val contents = HashMap<Long, TextContent>()
        if (articles.isNotEmpty()) {
            contents.putAll(TextService.getById(QueryTextReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.contentId })
                }
            }).result)
        }

        resp.header("Content-Type", "application/xml")
        return@Route encode(articles, users, contents)
    }

    val latest = Route { _, resp ->
        val articles = ArticleService.getLatest(QueryLatestArticleReq().apply {
            this.offset = 0
            this.limit = 20
            this.state = "${Article.State.FINE},${Article.State.NORMAL}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                }.distinctBy { it }
            }).info)
        }

        val contents = HashMap<Long, TextContent>()
        if (articles.isNotEmpty()) {
            contents.putAll(TextService.getById(QueryTextReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.contentId })
                }
            }).result)
        }

        resp.header("Content-Type", "application/xml")
        return@Route encode(articles, users, contents)
    }

    private fun encode(articles: List<Article>, users: HashMap<Long, UserInfo>,
                       contents: HashMap<Long, TextContent>): String {
        val df = SimpleDateFormat("E, MM dd yyyy HH:mm:ss z", Locale.US)
        val localeDivide = 14 * 60 * 60 * 1000
        val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument().apply {
            appendChild(createElement("rss").apply {
                setAttribute("version", "2.0")
                appendChild(createElement("channel").apply {
                    appendChild(createElement("title").apply { textContent = "Kotlin-CN 中文网" })
                    appendChild(createElement("link").apply { textContent = frontendHost })
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
                            appendChild(createElement("link").apply {
                                textContent = "$frontendHost/post/${it.id}"
                            })
                            appendChild(createElement("guid").apply {
                                textContent = "$frontendHost/post/${it.id}"
                            })
                        })
                    }
                })
            })
        }
        return StringWriter().apply {
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(DOMSource(doc), StreamResult(this))
        }.toString()
    }
}