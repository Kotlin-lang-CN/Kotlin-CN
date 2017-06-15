package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.account.req.*
import tech.kotlin.service.account.resp.CreateAccountResp
import tech.kotlin.service.account.resp.LoginResp
import tech.kotlin.service.domain.EmptyResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface AccountApi {

    @RpcInterface(TypeDef.Account.CREATE)
    fun create(req: CreateAccountReq): CreateAccountResp

    @RpcInterface(TypeDef.Account.LOGIN_WITH_NAME)
    fun loginWithName(req: LoginReq): LoginResp

    @RpcInterface(TypeDef.Account.CHANGE_USER_STATE)
    fun changeUserState(req: ChangeUserStateReq): EmptyResp

    @RpcInterface(TypeDef.Account.UPDATE_PASSWORD)
    fun updatePassword(req: UpdatePasswordReq): EmptyResp

}