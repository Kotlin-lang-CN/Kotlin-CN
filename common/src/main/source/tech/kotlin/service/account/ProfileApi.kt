package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.account.req.UpdateProfileReq
import tech.kotlin.service.account.req.UpdateUserReq
import tech.kotlin.service.account.resp.QueryProfileResp
import tech.kotlin.service.domain.EmptyResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ProfileApi {

    @RpcInterface(TypeDef.Profile.QUERY_BY_ID)
    fun queryById(req: QueryUserReq): QueryProfileResp

    @RpcInterface(TypeDef.Profile.UPDATE_BY_ID)
    fun updateById(req: UpdateProfileReq): EmptyResp

}