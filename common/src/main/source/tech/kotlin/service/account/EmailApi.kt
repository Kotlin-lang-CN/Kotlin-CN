package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.article.req.EmailCheckTokenReq
import tech.kotlin.service.account.req.CreateEmailSessionReq
import tech.kotlin.service.article.req.EmailReq
import tech.kotlin.service.account.resp.CreateEmailSessionResp
import tech.kotlin.service.account.resp.EmailCheckTokenResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.TypeDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface EmailApi {

    @RpcInterface(TypeDef.Email.CREATE_SESSION)
    fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp

    @RpcInterface(TypeDef.Email.CHECK_TOKEN)
    fun checkToken(req: EmailCheckTokenReq): EmailCheckTokenResp

    @RpcInterface(TypeDef.Email.SEND)
    fun send(req: EmailReq): EmptyResp

}