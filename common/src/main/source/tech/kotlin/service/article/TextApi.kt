package tech.kotlin.service.article

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.model.request.CreateTextContentReq
import tech.kotlin.model.request.QueryTextReq
import tech.kotlin.model.response.CreateTextContentResp
import tech.kotlin.model.response.QueryTextResp
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