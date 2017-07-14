package tech.kotlin.service.message

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.message.req.GroupReq
import tech.kotlin.service.message.req.ListGroupReq
import tech.kotlin.service.message.resp.ListGroupResp
import tech.kotlin.service.message.resp.QueryGroupStateResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface GroupApi {

    @RpcInterface(TypeDef.Message.QUERY_GROUP_STATE)
    fun queryGroupState(req: GroupReq): QueryGroupStateResp

    @RpcInterface(TypeDef.Message.JOIN_GROUP)
    fun joinGroup(req: GroupReq): EmptyResp

    @RpcInterface(TypeDef.Message.LEAVE_GROUP)
    fun leaveGroup(req: GroupReq): EmptyResp

    @RpcInterface(TypeDef.Message.LIST_GROUP)
    fun listGroup(req: ListGroupReq): ListGroupResp

    @RpcInterface(TypeDef.Message.COUNT_GROUP)
    fun countGroup(req: CountGroupReq): CountGroupResp


}

