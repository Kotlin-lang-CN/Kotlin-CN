package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.*
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.service.article.ArticleService
import tech.kotlin.service.article.TextService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleController {

    val postArticle = Route { req, _ ->
        val title = req.queryParams("title")
                .check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
        val author = req.queryParams("author")
                .check(Err.PARAMETER, "无效的用户") { it.toLong() > 0 }.toLong()
        val category = req.queryParams("category")
                .check(Err.PARAMETER, "无效的文章类型") { it.toInt() >= 0 }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val content = req.queryParams("content")
                .check(Err.PARAMETER, "无效的文章内容") { !it.isNullOrBlank() }
                .check(Err.PARAMETER, "文章内容过短") { it.length >= 30 }

        val account = TokenService.checkToken(CheckTokenReq(req)).account
        account.check(Err.UNAUTHORIZED) { it.id == author }

        val id = ArticleService.create(CreateArticleReq().apply {
            this.title = title
            this.author = author
            this.category = category
            this.tags = tags

            this.content = content
        }).articleId

        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.SYSTEM)

        return@Route ok { it["id"] = article.id }
    }

    val updateArticle = Route { req, _ ->
        val id = req.params(":id")
                .check(Err.PARAMETER, "无效的文章id") { it.toLong();true }.toLong()

        val title = req.queryParams("title") ?: ""

        val category = req.queryParams("category")?.apply {
            check(Err.PARAMETER, "无效的文章类别") { it.toInt();true }
        }?.toInt() ?: -1

        val tags = req.queryParams("tags") ?: ""

        val content = req.queryParams("content")?.apply {
            check(Err.PARAMETER, "内容过短") { it.length >= 30 }
        } ?: ""

        val me = TokenService.checkToken(CheckTokenReq(req)).account

        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        if (me.role != Account.Role.ADMIN && article.author != me.id) abort(Err.UNAUTHORIZED)

        var contentId = 0L
        if (!content.isNullOrBlank()) {
            contentId = ArticleService.updateContent(UpdateArticleContentReq().apply {
                this.content = content
                this.editorUid = me.id
                this.articleId = article.id
            }).contentId
        }

        ArticleService.updateMeta(UpdateArticleReq().apply {
            this.id = id
            this.args = HashMap<String, String>().apply {
                if (!title.isNullOrBlank()) this += "title" to title
                if (category > 0) this += "category" to "$category"
                if (!tags.isNullOrBlank()) this += "tags" to tags
                this += "last_edit_time" to "${System.currentTimeMillis()}"
                this += "last_edit_uid" to "${me.id}"
                if (contentId != 0L) this += "content_id" to "$contentId"
            }
        })

        return@Route ok()
    }

    val getArticleById = Route { req, _ ->
        val id = req.params(":id").check(Err.PARAMETER) { it.toLong();true }.toLong()

        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        val author = UserService.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).account[article.author] ?: UserInfo()

        val content = TextService.getById(QueryTextReq().apply {
            this.id = article.contentId
        }).result[article.contentId] ?: TextContent()

        return@Route ok {
            it["author"] = author
            it["article"] = article
            it["content"] = content
        }
    }
}