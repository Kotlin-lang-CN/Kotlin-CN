package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.dict
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Article
import tech.kotlin.model.domain.Reply
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.*
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.AccountApi
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AdminController {

    val accountApi by Serv.bind(AccountApi::class)
    val sessionApi by Serv.bind(SessionApi::class)
    val userApi by Serv.bind(UserApi::class)

    val articleApi by Serv.bind(ArticleApi::class, ServDef.ARTICLE)
    val replyApi by Serv.bind(ReplyApi::class, ServDef.ARTICLE)

    val userState = Route { req, _ ->
        val userId = req.params(":id")
                .check(Err.PARAMETER) { it.toLong();true }.toLong()

        val state = req.queryParams("state")
                .check(Err.PARAMETER) { s ->
                    arrayOf(Account.State.NORMAL, Account.State.BAN).any { it == s.toInt() }
                }.toInt()

        val owner = sessionApi.checkSession(CheckSessionReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        accountApi.changeUserState(ChangeUserStateReq().apply {
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

        val owner = sessionApi.checkSession(CheckSessionReq(req)).account
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

        val owner = sessionApi.checkSession(CheckSessionReq(req)).account
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

        val owner = sessionApi.checkSession(CheckSessionReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

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
}