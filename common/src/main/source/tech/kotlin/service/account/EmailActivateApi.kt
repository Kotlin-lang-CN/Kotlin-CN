package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.model.request.ActivateEmailReq
import tech.kotlin.model.request.CreateEmailSessionReq
import tech.kotlin.model.response.CreateEmailSessionResp
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface EmailActivateApi {

    @RpcInterface(TypeDef.EmailActivate.CREATE_SESSION)
    fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp

    @RpcInterface(TypeDef.EmailActivate.ACTIVATE)
    fun activate(req: ActivateEmailReq): EmptyResp

}