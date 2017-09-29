package cn.kotliner.forum.service.article.api

import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.service.article.req.QueryReplyCountByAuthorReq
import cn.kotliner.forum.service.article.resp.QueryReplyCountByAuthorResp
import cn.kotliner.forum.service.article.resp.CreateReplyResp
import cn.kotliner.forum.service.article.resp.QueryReplyByIdResp
import cn.kotliner.forum.service.article.resp.QueryReplyCountByArticleResp
import cn.kotliner.forum.service.article.resp.ReplyListResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ReplyApi {

    fun create(req: CreateArticleReplyReq): CreateReplyResp

    fun changeState(req: ChangeReplyStateReq)

    fun getReplyById(req: QueryReplyByIdReq): QueryReplyByIdResp

    fun getReplyByArticle(req: QueryReplyByArticleReq): ReplyListResp

    fun getReplyCountByArticle(req: QueryReplyCountByArticleReq): QueryReplyCountByArticleResp

    fun getReplyByAuthor(req: QuerReplyByAuthorReq): ReplyListResp

    fun getReplyCountByAuthor(req: QueryReplyCountByAuthorReq): QueryReplyCountByAuthorResp
}

