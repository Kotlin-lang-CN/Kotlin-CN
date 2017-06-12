package tech.kotlin.service.account

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.model.request.ChangeUserStateReq
import tech.kotlin.model.request.CreateAccountReq
import tech.kotlin.model.request.LoginReq
import tech.kotlin.model.request.UpdatePasswordReq
import tech.kotlin.model.response.CreateAccountResp
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.model.response.LoginResp
import tech.kotlin.service.TypeDef

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