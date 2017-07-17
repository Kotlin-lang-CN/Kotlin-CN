package tech.kotlin.service.message

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.message.req.*
import tech.kotlin.service.message.resp.QueryGroupStateResp
import tech.kotlin.service.message.resp.QueryMessageResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface MessageApi {

    @RpcInterface(TypeDef.Message.BROAD_CAST)
    fun broadcast(req: BroadcastReq): EmptyResp

    @RpcInterface(TypeDef.Message.LIST_CAST)
    fun listcast(req: ListcastReq): EmptyResp

    @RpcInterface(TypeDef.Message.GROUP_CAST)
    fun groupcast(req: GroupcastReq): EmptyResp

    @RpcInterface(TypeDef.Message.GET_BY_ACCEPTOR)
    fun getByAcceptor(req: QueryMessageReq): QueryMessageResp

    @RpcInterface(TypeDef.Message.MARK_READ)
    fun markReadReq(req: MarkReadReq): EmptyResp

}