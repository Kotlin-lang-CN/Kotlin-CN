package tech.kotlin.article.controller

import spark.Route
import tech.kotlin.article.API
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.exceptions.check
import tech.kotlin.model.Account
import tech.kotlin.model.UserInfo
import tech.kotlin.service.Node
import tech.kotlin.service.account.CheckTokenReq
import tech.kotlin.service.account.QueryUserReq
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.service.article.ArticleService
import tech.kotlin.service.article.CreateArticleReq
import tech.kotlin.service.article.QueryArticleByIdReq
import tech.kotlin.service.article.UpdateArticleReq

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleController {

    val tokenService: TokenService by lazy { Node[API.ACCOUNT][TokenService::class.java] }
    val userService: UserService by lazy { Node[API.ACCOUNT][UserService::class.java] }
    val articleService: ArticleService by lazy { Node[API.ARTICLE][ArticleService::class.java] }

    val postArticle = Route { req, _ ->
        val title = req.queryParams("title").check(Err.PARAMETER, "无效的用户名") { it.isNullOrBlank() }
        val author = req.queryParams("author").check(Err.PARAMETER, "无效的用户") { it.toLong() > 0 }.toLong()
        val category = req.queryParams("category").check(Err.PARAMETER, "无效的文章类型") { it.toInt() >= 0 }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val account = tokenService.checkToken(CheckTokenReq(req)).account
        account.check(Err.UNAUTHORIZED) { it.id == author }

        val article = articleService.create(CreateArticleReq().apply {
            this.title = title
            this.author = author
            this.category = category
            this.tags = tags
        }).article

        return@Route HashMap<String, Any>().apply {
            this["article"] = article
        }
    }

    val updateArticle = Route { req, _ ->
        val id = req.queryParams(":id").check(Err.PARAMETER) { it.toLong() > 0 }.toLong()
        val title = req.queryParams("title") ?: ""
        val category = req.queryParams("category").check(Err.PARAMETER) { it.toInt();true }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val account = tokenService.checkToken(CheckTokenReq(req)).account
        val article = articleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        if (account.role != Account.Role.ADMIN || article.author != account.id) abort(Err.UNAUTHORIZED)

        val newArticle = articleService.update(UpdateArticleReq().apply {
            this.id = id
            this.args = HashMap<String, String>().apply {
                if (!title.isNullOrBlank()) this += "title" to title
                if (category > 0) this += "category" to "$category"
                if (!tags.isNullOrBlank()) this += "tags" to tags
                this += "last_edit_time" to "${System.currentTimeMillis()}"
                this += "last_edit_uid" to "${account.id}"
            }
        }).article

        return@Route HashMap<String, Any>().apply {
            this["article"] = newArticle
        }
    }

    val getArticleById = Route { req, _ ->
        val id = req.queryParams(":id").check(Err.PARAMETER) { it.toLong() > 0 }.toLong()

        val article = articleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        val author = userService.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).account[article.author] ?: UserInfo()

        return@Route HashMap<String, Any>().apply {
            this["author"] = author
            this["article"] = article
        }
    }

}