package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.model.request.CheckSessionReq
import tech.kotlin.model.request.CreateSessionReq
import tech.kotlin.model.response.CheckSessionResp
import tech.kotlin.model.response.CreateSessionResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface SessionApi {

    @RpcInterface(TypeDef.Session.CREATE_SESSION)
    fun createSession(req: CreateSessionReq): CreateSessionResp

    @RpcInterface(TypeDef.Session.CHECK_SESSION)
    fun checkSession(req: CheckSessionReq): CheckSessionResp

}