package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.dict
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.Article
import tech.kotlin.service.domain.Reply
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.common.utils.ok
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.common.utils.check
import tech.kotlin.service.*
import tech.kotlin.service.account.req.ChangeUserStateReq
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.article.req.ChangeReplyStateReq
import tech.kotlin.service.article.req.QueryLatestArticleReq
import tech.kotlin.service.article.req.QueryReplyCountByArticleReq
import tech.kotlin.service.article.req.UpdateArticleReq

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AdminController {

    val articleApi by Serv.bind(ArticleApi::class, ServDef.ARTICLE)
    val replyApi by Serv.bind(ReplyApi::class, ServDef.ARTICLE)

    val userState = Route { req, _ ->
        val userId = req.params(":id")
                .check(Err.PARAMETER) { it.toLong();true }.toLong()

        val state = req.queryParams("state")
                .check(Err.PARAMETER) { s ->
                    arrayOf(Account.State.NORMAL, Account.State.BAN).any { it == s.toInt() }
                }.toInt()

        val owner = SessionService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        AccountService.changeUserState(ChangeUserStateReq().apply {
            this.uid = userId
            this.state = state
        })

        return@Route ok()
    }

    val articleState = Route { req, _ ->
        val articleId = req.params(":id")
                .check(Err.PARAMETER) { it.toLong();true }.toLong()

        val state = req.queryParams("state")
                .check(Err.PARAMETER) { s ->
                    arrayOf(Article.State.NORMAL, Article.State.BAN,
                            Article.State.DELETE, Article.State.FINE
                           ).any { it == s.toInt() }
                }

        val owner = SessionService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        articleApi.updateMeta(UpdateArticleReq().apply {
            this.id = articleId
            this.args = hashMapOf("state" to state)
        })

        return@Route ok()
    }

    val replyState = Route { req, _ ->
        val replyId = req.params(":id")
                .check(Err.PARAMETER) { it.toLong();true }.toLong()

        val state = req.queryParams("state").check(Err.PARAMETER) { s ->
            arrayOf(Reply.State.NORMAL, Reply.State.BAN, Reply.State.DELETE).any { it == s.toInt() }
        }.toInt()

        val owner = SessionService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        replyApi.changeState(ChangeReplyStateReq().apply {
            this.replyId = replyId
            this.state = state
        })

        return@Route ok()
    }

    val getArticleList = Route { req, _ ->
        val offset = req.queryParams("offset")
                             ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                             ?.toInt()
                     ?: 0

        val limit = req.queryParams("limit")
                            ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                            ?.toInt()
                    ?: 20

        val owner = SessionService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        val articles = articleApi.getLatest(QueryLatestArticleReq().apply {
            this.offset = offset
            this.limit = limit
            this.state = "${Article.State.FINE},${Article.State.NORMAL},${Article.State.BAN},${Article.State.DELETE}"
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(UserService.queryById(QueryUserReq().apply {
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

        return@Route ok {
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
}