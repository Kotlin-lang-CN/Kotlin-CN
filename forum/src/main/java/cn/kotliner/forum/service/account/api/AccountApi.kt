package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.ChangeUserStateReq
import cn.kotliner.forum.service.account.req.CreateAccountReq
import cn.kotliner.forum.service.account.req.LoginReq
import cn.kotliner.forum.service.account.req.UpdatePasswordReq
import cn.kotliner.forum.service.account.resp.CreateAccountResp
import cn.kotliner.forum.service.account.resp.LoginResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface AccountApi {

    fun initAdmin()

    fun create(req: CreateAccountReq): CreateAccountResp

    fun loginWithName(req: LoginReq): LoginResp

    fun changeUserState(req: ChangeUserStateReq)

    fun updatePassword(req: UpdatePasswordReq)

    fun getCount(): Int

}