package cn.kotliner.forum.controller.article

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.Article
import cn.kotliner.forum.domain.model.TextContent
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.ReplyApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.utils.*
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.ArrayList
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/creator")
class CreatorController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var replyApi: ReplyApi
    @Resource private lateinit var textApi: TextApi

    //获取我创作的文章
    @GetMapping("/article")
    fun getMyArticle(
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {
        val me = sessionApi.checkToken(req.token).account.id

        val articles = articleApi.getByAuthor(QueryByAuthorReq().apply {
            this.author = me
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
                    }).result
            )
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

    //获取我创作的评论
    @GetMapping("/reply")
    fun getMyReply(
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        val reply = replyApi.getReplyByAuthor(QuerReplyByAuthorReq().apply {
            this.author = me
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        val contents = HashMap<Long, TextContent>()
        if (reply.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = reply.map { it.ownerUID }.toList()
            }).info)
            contents.putAll(textApi.getById(QueryTextReq().apply {
                this.id = reply.map { it.contentId }.toList()
            }).result)
        }

        return ok {
            it["reply"] = reply.map {
                dict {
                    this["meta"] = it
                    this["user"] = users[it.ownerUID] ?: UserInfo()
                    this["content"] = contents[it.contentId] ?: TextContent()
                }
            }
            it["next_offset"] = offset + reply.size
        }
    }

    //获取用户的文章数
    @GetMapping("/article/count")
    fun getMyArticleCount(@RequestParam("id") id: String): Resp {
        val queryId = id
                .check(Err.PARAMETER) { it.split(',').map { it.toLong() };true }
                .split(',')
                .map { it.toLong() }

        val result = articleApi
                .countByAuthor(CountArticleByAuthorReq().apply { this.author = queryId })

        return ok {
            it["result"] = result.result
        }
    }

    //获取用户的回复数
    @GetMapping("/reply/count")
    fun getMyReplyCount(@RequestParam("id") id: String): Resp {
        val queryId = id
                .check(Err.PARAMETER) { it.split(',').map { it.toLong() };true }
                .split(',')
                .map { it.toLong() }

        val result = replyApi
                .getReplyCountByAuthor(QueryReplyCountByAuthorReq().apply { this.author = queryId })

        return ok {
            it["result"] = result.result
        }
    }
}