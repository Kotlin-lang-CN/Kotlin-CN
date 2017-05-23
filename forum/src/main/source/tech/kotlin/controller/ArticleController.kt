package tech.kotlin.controller

import spark.Route
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.service.article.ArticleService
import tech.kotlin.model.request.CreateArticleReq
import tech.kotlin.model.request.QueryArticleByIdReq
import tech.kotlin.model.request.UpdateArticleReq
import tech.kotlin.utils.ok

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleController {

    val postArticle = Route { req, _ ->
        val title = req.queryParams("title").check(Err.PARAMETER, "无效的用户名") { it.isNullOrBlank() }
        val author = req.queryParams("author").check(Err.PARAMETER, "无效的用户") { it.toLong() > 0 }.toLong()
        val category = req.queryParams("category").check(Err.PARAMETER, "无效的文章类型") { it.toInt() >= 0 }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val account = TokenService.checkToken(CheckTokenReq(req)).account
        account.check(Err.UNAUTHORIZED) { it.id == author }

        val article = ArticleService.create(CreateArticleReq().apply {
            this.title = title
            this.author = author
            this.category = category
            this.tags = tags
        }).article

        return@Route ok { it["article"] = article }
    }

    val updateArticle = Route { req, _ ->
        val id = req.queryParams(":id").check(Err.PARAMETER) { it.toLong() > 0 }.toLong()
        val title = req.queryParams("title") ?: ""
        val category = req.queryParams("category").check(Err.PARAMETER) { it.toInt();true }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val account = TokenService.checkToken(CheckTokenReq(req)).account
        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        if (account.role != Account.Role.ADMIN || article.author != account.id) abort(Err.UNAUTHORIZED)

        val newArticle = ArticleService.update(UpdateArticleReq().apply {
            this.id = id
            this.args = HashMap<String, String>().apply {
                if (!title.isNullOrBlank()) this += "title" to title
                if (category > 0) this += "category" to "$category"
                if (!tags.isNullOrBlank()) this += "tags" to tags
                this += "last_edit_time" to "${System.currentTimeMillis()}"
                this += "last_edit_uid" to "${account.id}"
            }
        }).article

        return@Route ok { it["article"] = newArticle }
    }

    val getArticleById = Route { req, _ ->
        val id = req.queryParams(":id").check(Err.PARAMETER) { it.toLong() > 0 }.toLong()

        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        val author = UserService.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).account[article.author] ?: UserInfo()

        return@Route ok {
            it["author"] = author
            it["article"] = article
        }
    }
}