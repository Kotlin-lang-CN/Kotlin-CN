package cn.kotliner.forum.controller.article

import cn.kotliner.forum.domain.Article
import cn.kotliner.forum.domain.TextContent
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.article.req.QueryLatestArticleReq
import cn.kotliner.forum.service.article.req.QueryTextReq
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@RestController
@RequestMapping("/api/rss")
@PropertySource("classpath:forum.properties")
class RssController {

    @Value("\${deploy.frontend.host}") private lateinit var frontendHost: String

    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var textApi: TextApi

    @RequestMapping("/fine", produces = arrayOf("application/xml"))
    fun getFine(resp: HttpServletResponse): String {
        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
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
            contents.putAll(textApi.getById(QueryTextReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.contentId })
                }
            }).result)
        }

        resp.setHeader("Content-Type", "application/xml")
        return encode(
                contents = contents,
                users = users,
                articles = articles
        )
    }

    @RequestMapping("/latest", produces = arrayOf("application/xml"))
    fun getLatest(resp: HttpServletResponse): String {
        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
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
            contents.putAll(textApi.getById(QueryTextReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.contentId })
                }
            }).result)
        }

        resp.setHeader("Content-Type", "application/xml")
        return encode(
                contents = contents,
                users = users,
                articles = articles
        )
    }

    private fun encode(
            contents: HashMap<Long, TextContent>,
            users: HashMap<Long, UserInfo>,
            articles: List<Article>
    ): String {

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
                            appendChild(createElement("title").apply {
                                textContent = it.title
                            })
                            appendChild(createElement("description").apply {
                                textContent = contents[it.contentId]!!.content
                            })
                            appendChild(createElement("author").apply {
                                textContent = users[it.author]!!.username
                            })
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
        // XML 1.1
        return StringWriter().apply {
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(DOMSource(doc), StreamResult(this))
        }.toString().replace(//xml1.0
                Regex("[^\u0009\r\n\u0020-\uD7FF\uE000-\uFFFD\ud800\udc00-\udbff\udfff]"), ""
        ).replace(//xml1.1
                Regex("[^\u0001-\uD7FF\uE000-\uFFFD\ud800\udc00-\udbff\udfff]+"), ""
        )
    }
}
