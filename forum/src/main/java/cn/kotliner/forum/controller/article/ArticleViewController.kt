package cn.kotliner.forum.controller.article

import cn.kotliner.forum.domain.Article
import cn.kotliner.forum.domain.Category
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.ReplyApi
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.dict
import cn.kotliner.forum.utils.gateway.ok
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.req.QueryLatestArticleReq
import cn.kotliner.forum.service.article.req.QueryReplyCountByArticleReq
import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.ArrayList
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/article")
class ArticleViewController {

    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var replyApi: ReplyApi

    //获取最新的文章列表
    @GetMapping("/list")
    fun getLatest(
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20L)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {

        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        val replies = HashMap<Long, Int>()
        if (articles.isNotEmpty()) {
            replies.putAll(replyApi.getReplyCountByArticle(
                    QueryReplyCountByArticleReq().apply {
                        this.id = articles.map { it.id }.toList()
                    }).result)
        }

        return ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                    this["replies"] = replies[it.id] ?: 0
                    this["is_fine"] = it.state == Article.State.FINE
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }


    @GetMapping("/fine")
    fun getFine(
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20L)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {
        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.state = "${Article.State.FINE}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        val replies = HashMap<Long, Int>()
        if (articles.isNotEmpty()) {
            replies.putAll(replyApi.getReplyCountByArticle(QueryReplyCountByArticleReq().apply {
                this.id = articles.map { it.id }.toList()
            }).result)
        }

        return ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                    this["replies"] = replies[it.id] ?: 0
                    this["is_fine"] = it.state == Article.State.FINE
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }

    //获取分类别文章列表
    @GetMapping("/category/{id}")
    fun getByCategory(
            @PathVariable("id") category: Int,
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20L)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {

        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.category = "$category"

        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        val replies = HashMap<Long, Int>()
        if (articles.isNotEmpty()) {
            replies.putAll(replyApi.getReplyCountByArticle(QueryReplyCountByArticleReq().apply {
                this.id = articles.map { it.id }.toList()
            }).result)
        }

        return ok {
            it["articles"] = articles.map {
                dict {
                    this["meta"] = it
                    this["author"] = users[it.author] ?: UserInfo()
                    this["last_editor"] = users[it.lastEditUID] ?: UserInfo()
                    this["replies"] = replies[it.id] ?: 0
                    this["is_fine"] = it.state == Article.State.FINE
                }
            }
            it["next_offset"] = offset + articles.size
        }
    }


    //获取文章分类
    @GetMapping("/category")
    fun getCategories() = ok {
        it["category"] = Category.values().map { it.value }
    }

    //获取文章总数
    @GetMapping("/count")
    fun getCount() = ok {
        it["total"] = articleApi.countAll()
    }
}