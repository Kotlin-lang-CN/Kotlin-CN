package tech.kotlin.service.message

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.domain.EmptyResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface MessageApi {

    @RpcInterface(TypeDef.Message.BROAD_CAST)
    fun broadcast(req: BroadcastReq): SendMessageResp

    @RpcInterface(TypeDef.Message.LIST_CAST)
    fun listcast(req: ListcastReq): SendMessageResp

    @RpcInterface(TypeDef.Message.GET_BY_USER)
    fun getByUser(req: QueryMessageReq): QueryMessageResp

    @RpcInterface(TypeDef.Message.CHANGE_STATE)
    fun changeState(req: ChangeStateReq): EmptyResp

}

class QueryMessageReq {
}

class QueryMessageResp {

}

class ChangeStateReq {

}