package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.dict
import tech.kotlin.service.domain.Article
import tech.kotlin.service.domain.Category
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.article.req.QueryLatestArticleReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.Err
import tech.kotlin.common.utils.check
import tech.kotlin.service.ArticleService
import tech.kotlin.service.ReplyService
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.article.req.QueryReplyCountByArticleReq
import java.util.*
import kotlin.collections.HashMap


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleViewController {

    val userApi by Serv.bind(UserApi::class, ServDef.ACCOUNT)

    val getList = Route { req, _ ->
        val offset = req.queryParams("offset")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val limit = req.queryParams("limit")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 20

        val articles = ArticleService.getLatest(QueryLatestArticleReq().apply {
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

        val articles = ArticleService.getLatest(QueryLatestArticleReq().apply {
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

    val getFine = Route { req, _ ->
        val offset = req.queryParams("offset")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 0

        val limit = req.queryParams("limit")
                ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                ?.toInt()
                ?: 20

        val articles = ArticleService.getLatest(QueryLatestArticleReq().apply {
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

    val getCategory = Route { _, _ -> ok { it["category"] = Category.values().map { it.value } } }

    val getCount = Route { _, _ -> ok { it["total"] = ArticleService.countAll().result } }

}