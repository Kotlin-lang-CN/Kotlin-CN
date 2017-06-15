package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.account.req.GithubAuthReq
import tech.kotlin.service.account.req.GithubCreateStateReq
import tech.kotlin.service.account.resp.GithubCheckTokenResp
import tech.kotlin.service.account.resp.GithubAuthResp
import tech.kotlin.service.account.resp.GithubCheckTokenReq
import tech.kotlin.service.account.resp.GithubCreateStateResp
import tech.kotlin.service.TypeDef
import tech.kotlin.service.account.req.GithubBindReq
import tech.kotlin.service.domain.EmptyResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface GithubApi {

    @RpcInterface(TypeDef.Github.CREATE_STATE)
    fun createState(req: GithubCreateStateReq): GithubCreateStateResp

    @RpcInterface(TypeDef.Github.CREATE_SESSION)
    fun createSession(req: GithubAuthReq): GithubAuthResp

    @RpcInterface(TypeDef.Github.CHECK_TOKEN)
    fun checkToken(req: GithubCheckTokenReq): GithubCheckTokenResp

    @RpcInterface(TypeDef.Github.BIND_ACCOUNT)
    fun bindAccount(req: GithubBindReq): EmptyResp

}


