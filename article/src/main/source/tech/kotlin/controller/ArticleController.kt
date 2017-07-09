package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.strDict
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.TextApi
import tech.kotlin.service.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.tryExec
import tech.kotlin.service.ArticleService
import tech.kotlin.service.TextService
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.article.req.*
import tech.kotlin.service.domain.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)
    val userApi by Serv.bind(UserApi::class, ServDef.ACCOUNT)

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

        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        account.check(Err.UNAUTHORIZED) { it.id == author }
                .check(Err.UNAUTHORIZED, "只有管理员才能发布【站务】话题") {
                    category != Category.STATION.ordinal + 1 || it.role == Account.Role.ADMIN
                }

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

        //只有作者和管理员才能修改文章内容, 只有管理员才能发布站务话题
        val me = sessionApi.checkToken(CheckTokenReq(req)).account
                .check(Err.UNAUTHORIZED, "只有管理员才能发布【站务】话题") {
                    category != Category.STATION.ordinal + 1 || it.role == Account.Role.ADMIN
                }

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
        val me = sessionApi.checkToken(CheckTokenReq(req)).account
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
                val account = sessionApi.checkToken(CheckTokenReq(req)).account
                assert(account.role == Account.Role.ADMIN)
            }
        }

        val author = userApi.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).info[article.author] ?: UserInfo()

        val lastEditor = userApi.queryById(QueryUserReq().apply {
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