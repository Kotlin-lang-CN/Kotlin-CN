package tech.kotlin.service.article

import com.relops.snowflake.Snowflake
import tech.kotlin.dao.article.ArticleDao
import tech.kotlin.dao.article.TextDao
import tech.kotlin.model.domain.Article
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.request.CreateArticleReq
import tech.kotlin.model.request.QueryArticleByIdReq
import tech.kotlin.model.request.UpdateArticleContentReq
import tech.kotlin.model.request.UpdateArticleReq
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
        val current = System.currentTimeMillis()
        val contentId = Snowflake(0).next()
        val articleId = Snowflake(0).next()
        val article = Article().apply {
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
        }
        val textContent = TextContent().apply {
            this.id = contentId
            this.content = req.content
            this.type = TextContent.Type.ARTICLE
            this.createTime = current
            this.aliasId = article.id
        }
        Mysql.write {
            ArticleDao.createOrUpdate(it, article)
            TextDao.create(it, textContent)
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
        val article = Mysql.write {
            val article = ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
            val current = System.currentTimeMillis()
            val textContent = TextContent().apply {
                this.id = Snowflake(0).next()
                this.content = req.content
                this.type = TextContent.Type.ARTICLE
                this.createTime = current
                this.aliasId = article.id
            }
            ArticleDao.createOrUpdate(it, article.apply {
                this.lastEdit = current
                this.lastEditUID = req.editorUid
                this.contentId = textContent.id
            })
            return@write article
        }
        return ArticleResp().apply {
            this.articleId = article.id
            this.contentId = article.contentId
        }
    }

    fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp {
        val result = HashMap<Long, Article>()
        Mysql.read { session ->
            req.ids.forEach { id ->
                val article = ArticleDao.getById(session, id)
                if (article != null) result[id] = article
            }
        }
        return QueryArticleByIdResp().apply {
            this.articles = result
        }
    }

}

