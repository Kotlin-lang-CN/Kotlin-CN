package tech.kotlin.service.article

import com.relops.snowflake.Snowflake
import tech.kotlin.dao.article.ArticleDao
import tech.kotlin.dao.article.TextDao
import tech.kotlin.model.domain.Article
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.request.*
import tech.kotlin.model.response.ArticleResp
import tech.kotlin.model.response.QueryArticleByIdResp
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleService {

    fun create(req: CreateArticleReq): ArticleResp {
        val articleId = Snowflake(0).next()
        val current = System.currentTimeMillis()
        //调用文本服务创建新的文本对象
        val contentId = TextService.createContent(CreateTextContentReq().apply {
            this.content = req.content
            this.serializeId = "article:$articleId"
        }).id

        //存储文本数据
        Mysql.write {
            ArticleDao.createOrUpdate(it, Article().apply {
                this.id = articleId
                this.title = req.title
                this.author = req.author
                this.createTime = current
                this.category = req.category
                this.tags = req.tags
                this.lastEdit = current
                this.lastEditUID = req.author
                this.state = Article.State.NORMAL
                this.contentId = contentId
            })
        }
        return ArticleResp().apply {
            this.articleId = articleId
            this.contentId = contentId
        }
    }

    fun updateMeta(req: UpdateArticleReq): ArticleResp {
        val article = Mysql.write {
            ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
            val args = HashMap<String, Any>().apply { putAll(req.args) }
            ArticleDao.update(it, req.id, args)
            return@write ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
        }
        return ArticleResp().apply {
            this.articleId = articleId
            this.contentId = article.contentId
        }
    }

    fun updateContent(req: UpdateArticleContentReq): ArticleResp {
        //调用文本服务创建新的文本对象
        val contentID = TextService.createContent(CreateTextContentReq().apply {
            this.content = req.content
            this.serializeId = "article:${req.articleId}"
        }).id

        //更新文章元数据
        return updateMeta(UpdateArticleReq().apply {
            this.id = req.articleId
            this.args = hashMapOf(
                    "last_edit_time" to "${System.currentTimeMillis()}",
                    "last_edit_uid" to "${req.editorUid}",
                    "content_id" to "$contentID"
            )
        })
    }

    fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp {
        val result = HashMap<Long, Article>()
        Mysql.read { session ->
            req.ids.forEach { id ->
                val article = ArticleDao.getById(session, id, useCache = true, updateCache = true)
                if (article != null) result[id] = article
            }
        }
        return QueryArticleByIdResp().apply {
            this.articles = result
        }
    }

}

