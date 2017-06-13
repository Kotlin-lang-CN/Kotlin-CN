package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.CreateSessionReq
import tech.kotlin.service.account.resp.CheckTokenResp
import tech.kotlin.service.account.resp.CreateSessionResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface SessionApi {

    @RpcInterface(TypeDef.Session.CREATE_SESSION)
    fun createSession(req: CreateSessionReq): CreateSessionResp

    @RpcInterface(TypeDef.Session.CHECK_SESSION)
    fun checkToken(req: CheckTokenReq): CheckTokenResp

}