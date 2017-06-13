package tech.kotlin.service.article

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.article.resp.ArticleResp
import tech.kotlin.service.article.resp.QueryArticleByIdResp
import tech.kotlin.service.TypeDef
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.ArticleListResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ArticleApi {

    @RpcInterface(TypeDef.Article.CREATE)
    fun create(req: CreateArticleReq): ArticleResp

    @RpcInterface(TypeDef.Article.UPDATE_META)
    fun updateMeta(req: UpdateArticleReq): ArticleResp

    @RpcInterface(TypeDef.Article.UPDATE_CONTENT)
    fun updateContent(req: UpdateArticleContentReq): ArticleResp

    @RpcInterface(TypeDef.Article.QUERY_BY_ID)
    fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp

    @RpcInterface(TypeDef.Article.GET_LATEST)
    fun getLatest(req: QueryLatestArticleReq): ArticleListResp

}