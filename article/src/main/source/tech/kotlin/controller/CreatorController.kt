package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.dict
import tech.kotlin.common.utils.ok
import tech.kotlin.service.*
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.article.QueryReplyCountByAuthorReq
import tech.kotlin.service.article.req.*
import tech.kotlin.service.domain.*
import java.util.ArrayList

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object CreatorController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)

    //获取我创作的回复
    val getMyReplies = Route { req, _ ->
        val offset = req.queryParams("offset")
                             ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                             ?.toInt()
                     ?: 0

        val limit = req.queryParams("limit")
                            ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                            ?.toInt()
                    ?: 20

        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id

        val reply = ReplyService.getReplyByAuthor(QuerReplyByAuthorReq().apply {
            this.author = me
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        val contents = HashMap<Long, TextContent>()
        if (reply.isNotEmpty()) {
            users.putAll(ReplyController.userApi.queryById(QueryUserReq().apply {
                this.id = reply.map { it.ownerUID }.toList()
            }).info)
            contents.putAll(TextService.getById(QueryTextReq().apply {
                this.id = reply.map { it.contentId }.toList()
            }).result)
        }

        return@Route ok {
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

    //获取我创作的文章
    val getMyArticles = Route { req, _ ->
        val offset = req.queryParams("offset")
                             ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                             ?.toInt()
                     ?: 0

        val limit = req.queryParams("limit")
                            ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                            ?.toInt()
                    ?: 20

        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id

        val articles = ArticleService.getByAuthor(QueryByAuthorReq().apply {
            this.author = me
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        if (articles.isNotEmpty()) {
            users.putAll(ArticleViewController.userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(articles.map { it.author })
                    addAll(articles.map { it.lastEditUID })
                }.distinctBy { it }
            }).info)
        }

        val replies = HashMap<Long, Int>()
        if (articles.isNotEmpty()) {
            replies.putAll(ReplyService.getReplyCountByArticle(QueryReplyCountByArticleReq().apply {
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

    //我创作的回复数
    val getReplyCount = Route { req, _ ->
        val queryId = req.queryParams("id")
                              ?.check(Err.PARAMETER) { it.split(',').map { it.toLong() };true }
                              ?.split(',')
                              ?.map { it.toLong() }
                      ?: abort(Err.PARAMETER, "no query id")
        val result = ReplyService.getReplyCountByAuthor(QueryReplyCountByAuthorReq().apply {
            this.author = queryId
        })
        return@Route ok { it["result"] = result.result }
    }

    //我创作的文章数
    val getArticleCount = Route { req, _ ->
        val queryId = req.queryParams("id")
                              ?.check(Err.PARAMETER) { it.split(',').map { it.toLong() };true }
                              ?.split(',')
                              ?.map { it.toLong() }
                      ?: abort(Err.PARAMETER, "no query id")
        val result = ArticleService.countByAuthor(CountArticleByAuthorReq().apply {
            this.author = queryId
        })
        return@Route ok { it["result"] = result.result }
    }

}