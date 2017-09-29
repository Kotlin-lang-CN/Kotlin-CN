package cn.kotliner.forum.service.article.impl

import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.dao.ArticleRepository
import cn.kotliner.forum.domain.Article
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.service.article.resp.ArticleListResp
import cn.kotliner.forum.service.article.resp.ArticleResp
import cn.kotliner.forum.service.article.resp.CountArticleByAuthorResp
import cn.kotliner.forum.service.article.resp.QueryArticleByIdResp
import com.github.pagehelper.PageHelper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
@Component
class ArticleService : ArticleApi {

    @Resource private lateinit var articleRepo: ArticleRepository
    @Resource private lateinit var textApi: TextApi

    //创建一篇文章
    @Transactional(readOnly = false)
    override fun create(req: CreateArticleReq): ArticleResp {
        val articleId = IDs.next()
        val current = System.currentTimeMillis()

        //调用文本服务创建新的文本对象
        val contentId = textApi.createContent(CreateTextContentReq().apply {
            this.content = req.content
            this.serializeId = "article:$articleId"
        }).id

        //存储文本数据
        articleRepo.createOrUpdate(Article().apply {
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
        return ArticleResp().apply {
            this.articleId = articleId
            this.contentId = contentId
        }
    }

    //更新文章元数据
    @Transactional(readOnly = false)
    override fun updateMeta(req: UpdateArticleReq): ArticleResp {
        articleRepo.getById(req.id, useCache = false) ?: abort(Err.ARTICLE_NOT_EXISTS)

        val args = HashMap<String, Any>().apply { putAll(req.args) }
        articleRepo.update(req.id, args)

        val article = articleRepo.getById(req.id, useCache = false) ?: abort(Err.ARTICLE_NOT_EXISTS)

        return ArticleResp().apply {
            this.articleId = article.id
            this.contentId = article.contentId
        }
    }

    //更新文章内容
    @Transactional(readOnly = false)
    override fun updateContent(req: UpdateArticleContentReq): ArticleResp {

        //调用文本服务创建新的文本对象
        val contentID = textApi.createContent(CreateTextContentReq().apply {
            this.content = req.content
            this.serializeId = "article:${req.articleId}"
        }).id

        return updateMeta(UpdateArticleReq().apply {
            this.id = req.articleId
            this.args = hashMapOf("last_edit_time" to "${System.currentTimeMillis()}",
                    "last_edit_uid" to "${req.editorUid}",
                    "content_id" to "$contentID")
        })
    }

    //通过id批量查询文章
    @Transactional(readOnly = true)
    override fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp {
        val result = HashMap<Long, Article>()
        req.ids.forEach { id ->
            val article = articleRepo.getById(id, useCache = true)
            if (article != null) result[id] = article
        }
        return QueryArticleByIdResp().apply {
            this.articles = result
        }
    }

    //查询最新的文章
    @Transactional(readOnly = true)
    override fun getLatest(req: QueryLatestArticleReq): ArticleListResp {
        val result = PageHelper.offsetPage<Article>(req.offset, req.limit)
                .doSelectPageInfo<Article> {
                    articleRepo.getLatest(args = HashMap<String, String>().apply {
                        if (!req.category.isBlank()) this["category"] = req.category
                        if (!req.state.isBlank()) this["state"] = req.state
                    }, updateCache = true)
                }.list

        return ArticleListResp().apply {
            this.result = result
        }
    }

    //根据用户查询文章
    @Transactional(readOnly = true)
    override fun getByAuthor(req: QueryByAuthorReq): ArticleListResp {
        val result = PageHelper.offsetPage<Article>(req.offset, req.limit)
                .doSelectPageInfo<Article> {
                    articleRepo.getByAuthor(id = req.author, useCache = true)
                }.list

        return ArticleListResp().apply {
            this.result = result
        }
    }

    //获取用户查询
    @Transactional(readOnly = true)
    override fun countByAuthor(req: CountArticleByAuthorReq): CountArticleByAuthorResp {
        val result = req.author.map { author -> author to articleRepo.countByAuthor(author) }.toMap()

        return CountArticleByAuthorResp().apply {
            this.result = result
        }
    }

    //查询用户总数
    override fun countAll(): Int {
        return articleRepo.countAll()
    }

}