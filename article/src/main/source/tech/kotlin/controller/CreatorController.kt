package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ArticleService
import tech.kotlin.service.Err
import tech.kotlin.service.ReplyService
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.article.QueryReplyCountByAuthorReq
import tech.kotlin.service.article.req.CountArticleByAuthorReq
import tech.kotlin.service.article.req.QuerReplyByAuthorReq
import tech.kotlin.service.article.req.QueryByAuthorReq

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

        val result = ReplyService.getReplyByAuthor(QuerReplyByAuthorReq().apply {
            this.author = me
            this.offset = offset
            this.limit = limit
        })

        return@Route ok {
            it["result"] = result.result
            it["next_offset"] = offset + result.result.size
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

        val result = ArticleService.getByAuthor(QueryByAuthorReq().apply {
            this.author = me
            this.offset = offset
            this.limit = limit
        })

        return@Route ok {
            it["result"] = result.result
            it["next_offset"] = offset + result.result.size
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