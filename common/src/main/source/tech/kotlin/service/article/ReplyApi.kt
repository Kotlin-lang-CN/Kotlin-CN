package tech.kotlin.service.article

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.CreateReplyResp
import tech.kotlin.service.article.resp.QueryReplyByArticleResp
import tech.kotlin.service.article.resp.QueryReplyByIdResp
import tech.kotlin.service.article.resp.QueryReplyCountByArticleResp
import tech.kotlin.service.domain.EmptyResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ReplyApi {

    @RpcInterface(TypeDef.Reply.CREATE)
    fun create(req: CreateArticleReplyReq): CreateReplyResp

    @RpcInterface(TypeDef.Reply.CHANGE_STATE)
    fun changeState(req: ChangeReplyStateReq): EmptyResp

    @RpcInterface(TypeDef.Reply.GET_REPLY_BY_ID)
    fun getReplyById(req: QueryReplyByIdReq): QueryReplyByIdResp

    @RpcInterface(TypeDef.Reply.GET_REPLY_BY_ARTICLE)
    fun getReplyByArticle(req: QueryReplyByArticleReq): QueryReplyByArticleResp

    @RpcInterface(TypeDef.Reply.GET_REPLY_COUNT_BY_ARTICLE)
    fun getReplyCountByArticle(req: QueryReplyCountByArticleReq): QueryReplyCountByArticleResp

}