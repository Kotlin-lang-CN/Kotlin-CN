package tech.kotlin.service.article

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.article.req.CreateTextContentReq
import tech.kotlin.service.article.req.QueryTextReq
import tech.kotlin.service.article.resp.CreateTextContentResp
import tech.kotlin.service.article.resp.QueryTextResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface TextApi {

    @RpcInterface(TypeDef.Text.GET_BY_ID)
    fun getById(req: QueryTextReq): QueryTextResp

    @RpcInterface(TypeDef.Text.CREATE_CONTENT)
    fun createContent(req: CreateTextContentReq): CreateTextContentResp

}