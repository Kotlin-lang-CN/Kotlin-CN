package cn.kotliner.forum.controller.account

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.Article
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.AccountApi
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.account.req.ChangeUserStateReq
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.ReplyApi
import cn.kotliner.forum.service.article.req.ChangeReplyStateReq
import cn.kotliner.forum.service.article.req.QueryLatestArticleReq
import cn.kotliner.forum.service.article.req.QueryReplyCountByArticleReq
import cn.kotliner.forum.service.article.req.UpdateArticleReq
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.utils.dict
import cn.kotliner.forum.utils.gateway.ok
import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/admin")
class AdminController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var accountApi: AccountApi
    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var replyApi: ReplyApi

    @GetMapping("/article/list")
    fun getAllArticles(
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.state = "${Article.State.FINE},${Article.State.NORMAL},${Article.State.BAN},${Article.State.DELETE}"
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

    @PostMapping("/user/{id}/state")
    fun updateUserState(
            @PathVariable("id") userId: Long,
            @RequestParam("state") state: Int
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        accountApi.changeUserState(ChangeUserStateReq().apply {
            this.uid = userId
            this.state = state
        })
        return ok()
    }

    @PostMapping("/article/{id}/state")
    fun updateArticleState(
            @PathVariable("id") articleId: Long,
            @RequestParam("state") state: Int
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        articleApi.updateMeta(UpdateArticleReq().apply {
            this.id = articleId
            this.args = mapOf("state" to "$state")
        })
        return ok()
    }

    @PostMapping("/reply/{id}state")
    fun updateReplyState(
            @PathVariable("id") replyId: Long,
            @RequestParam("state") state: Int
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        replyApi.changeState(ChangeReplyStateReq().apply {
            this.replyId = replyId
            this.state = state
        })
        return ok()
    }
}