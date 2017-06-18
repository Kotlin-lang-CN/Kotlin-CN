package tech.kotlin.service

import com.github.pagehelper.PageHelper
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.domain.Article
import tech.kotlin.service.article.resp.ArticleResp
import tech.kotlin.service.article.resp.QueryArticleByIdResp
import tech.kotlin.common.utils.IDs
import tech.kotlin.common.utils.abort
import tech.kotlin.dao.ArticleDao
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.TextApi
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.ArticleListResp
import tech.kotlin.utils.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Articles : ArticleApi {

    val textApi by Serv.bind(TextApi::class)

    //创建一篇文章
    override fun create(req: CreateArticleReq): ArticleResp {
        val articleId = IDs.next()
        val current = System.currentTimeMillis()
        //调用文本服务创建新的文本对象
        val contentId = textApi.createContent(CreateTextContentReq().apply {
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

    //更新文章元数据
    override fun updateMeta(req: UpdateArticleReq): ArticleResp {
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

    //更新文章内容
    override fun updateContent(req: UpdateArticleContentReq): ArticleResp {
        //调用文本服务创建新的文本对象
        val contentID = textApi.createContent(CreateTextContentReq().apply {
            this.content = req.content
            this.serializeId = "article:${req.articleId}"
        }).id

        return updateMeta(UpdateArticleReq().apply {
            this.id = req.articleId
            this.args = hashMapOf(
                    "last_edit_time" to "${System.currentTimeMillis()}",
                    "last_edit_uid" to "${req.editorUid}",
                    "content_id" to "$contentID"
            )
        })
    }

    //通过id批量查询文章
    override fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp {
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

    //查询最新的文章
    override fun getLatest(req: QueryLatestArticleReq): ArticleListResp {
        val result = Mysql.read {
            PageHelper.offsetPage<Article>(req.offset, req.limit
            ).doSelectPageInfo<Article> {
                ArticleDao.getLatest(it, args = HashMap<String, String>().apply {
                    if (!req.category.isNullOrBlank()) this["category"] = req.category
                    if (!req.state.isNullOrBlank()) this["state"] = req.state
                }, updateCache = true)
            }.list
        }
        return ArticleListResp().apply {
            this.result = result
        }
    }
}

