package tech.kotlin.article.service

import com.relops.snowflake.Snowflake
import tech.kotlin.article.dao.ArticleDao
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.model.Article
import tech.kotlin.model.EmptyResp
import tech.kotlin.mysql.Mysql
import tech.kotlin.service.article.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleServiceImpl : ArticleService {

    override fun create(req: CreateArticleReq): ArticleResp {
        val current = System.currentTimeMillis()
        val article = Article().apply {
            id = Snowflake(0).next()
            title = req.title
            author = req.author
            createTime = current
            category = req.category
            tags = req.tags
            lastEdit = current
            lastEditUID = req.author
            state = Article.State.NORMAL
        }
        Mysql.write { ArticleDao.createOrUpdate(it, article) }
        return ArticleResp().apply {
            this.article = article
        }
    }

    override fun update(req: UpdateArticleReq): ArticleResp {
        val article = Mysql.write {
            ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
            val args = HashMap<String, Any>().apply { putAll(req.args) }
            ArticleDao.update(it, req.id, args)
            return@write ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
        }
        return ArticleResp().apply {
            this.article = article
        }
    }

    override fun changeState(req: ChangeArticleStateReq): ArticleResp {
        val article = Mysql.write {
            ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
            val args = HashMap<String, Any>()
            args ["sate"] = req.state
            ArticleDao.update(it, req.id, args)
            return@write ArticleDao.getById(it, req.id) ?: abort(Err.ARTICLE_NOT_EXISTS)
        }
        return ArticleResp().apply {
            this.article = article
        }
    }

    override fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp {
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

    override fun queryInOrder(req: QueryArticleInOrderReq): QueryArticleInOrderResp {
        abort(Err.SYSTEM, "not implements yet")
    }

}