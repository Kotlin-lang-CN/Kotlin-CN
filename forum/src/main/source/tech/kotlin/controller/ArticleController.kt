package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Article
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
import tech.kotlin.utils.exceptions.tryExec
import tech.kotlin.utils.serialize.strDict

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleController {

    val postArticle = Route { req, _ ->
        val title = req.queryParams("title")
                .check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
        val author = req.queryParams("author")
                .check(Err.PARAMETER, "无效的用户") { it.toLong(); true }.toLong()
        val category = req.queryParams("category")
                .check(Err.PARAMETER, "无效的文章类型") { it.toInt() > 0 }.toInt()
        val tags = req.queryParams("tags") ?: ""

        val content = req.queryParams("content")
                .check(Err.PARAMETER, "无效的文章内容") { !it.isNullOrBlank() && it.trim().length >= 2 }
                .check(Err.PARAMETER, "文章内容过短") { !it.isNullOrBlank() && it.trim().length >= 30 }

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
            check(Err.PARAMETER, "内容过短") { !it.isNullOrBlank() && it.trim().length >= 30 }
        } ?: ""

        //只有作者和管理员才能修改文章内容
        val me = TokenService.checkToken(CheckTokenReq(req)).account
        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)
        if (me.role != Account.Role.ADMIN && article.author != me.id) abort(Err.UNAUTHORIZED)

        //生成文本对象
        var contentId = 0L
        if (!content.isNullOrBlank()) {
            contentId = ArticleService.updateContent(UpdateArticleContentReq().apply {
                this.content = content
                this.editorUid = me.id
                this.articleId = article.id
            }).contentId
        }

        //更新文章元数据
        ArticleService.updateMeta(UpdateArticleReq().apply {
            this.id = id
            this.args = strDict {
                if (!title.isNullOrBlank()) this += "title" to title
                if (category > 0) this += "category" to "$category"
                if (!tags.isNullOrBlank()) this += "tags" to tags
                this["last_edit_time"] = "${System.currentTimeMillis()}"
                this["last_edit_uid"] = "${me.id}"
                if (contentId != 0L) this += "content_id" to "$contentId"
            }
        })

        return@Route ok()
    }

    val deleteArticle = Route { req, _ ->
        val id = req.params(":id")
                .check(Err.PARAMETER, "无效的文章id") { it.toLong();true }.toLong()

        //只有作者和管理员才能删除文章
        val me = TokenService.checkToken(CheckTokenReq(req)).account
        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)
        if (me.role != Account.Role.ADMIN && article.author != me.id) abort(Err.UNAUTHORIZED)

        //跟新文章元数据
        ArticleService.updateMeta(UpdateArticleReq().apply {
            this.id = id
            this.args = hashMapOf("state" to "${Article.State.DELETE}")
        })

        return@Route ok()
    }

    val getArticleById = Route { req, _ ->
        val id = req.params(":id").check(Err.PARAMETER) { it.toLong();true }.toLong()

        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        //只有管理员才能看到封禁和删除的文章内容
        if (article.state == Article.State.BAN || article.state == Article.State.DELETE) {
            tryExec(Err.ARTICLE_NOT_EXISTS) {
                val account = TokenService.checkToken(CheckTokenReq(req)).account
                assert(account.role == Account.Role.ADMIN)
            }
        }

        val author = UserService.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).info[article.author] ?: UserInfo()

        val lastEditor = UserService.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.lastEditUID)
        }).info[article.author] ?: UserInfo()

        val content = TextService.getById(QueryTextReq().apply {
            this.id = arrayListOf(article.contentId)
        }).result[article.contentId] ?: TextContent()

        return@Route ok {
            it["author"] = author
            it["article"] = article
            it["content"] = content
            it["last_editor"] = lastEditor
        }
    }

}