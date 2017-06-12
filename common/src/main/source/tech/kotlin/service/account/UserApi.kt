package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.model.request.UpdateUserReq
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.model.response.QueryUserResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface UserApi {

    @RpcInterface(TypeDef.User.QUERY_BY_ID)
    fun queryById(req: QueryUserReq): QueryUserResp

    @RpcInterface(TypeDef.User.UPDATE_BY_ID)
    fun updateById(req: UpdateUserReq): EmptyResp

}