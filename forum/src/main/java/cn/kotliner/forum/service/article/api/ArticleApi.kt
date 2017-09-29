package cn.kotliner.forum.service.article.api

import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.service.article.resp.ArticleListResp
import cn.kotliner.forum.service.article.resp.ArticleResp
import cn.kotliner.forum.service.article.resp.CountArticleByAuthorResp
import cn.kotliner.forum.service.article.resp.QueryArticleByIdResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ArticleApi {

    fun create(req: CreateArticleReq): ArticleResp

    fun updateMeta(req: UpdateArticleReq): ArticleResp

    fun updateContent(req: UpdateArticleContentReq): ArticleResp

    fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp

    fun getLatest(req: QueryLatestArticleReq): ArticleListResp

    fun getByAuthor(req: QueryByAuthorReq): ArticleListResp

    fun countByAuthor(req: CountArticleByAuthorReq): CountArticleByAuthorResp

    fun countAll(): Int

}

