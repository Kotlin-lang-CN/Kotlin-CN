package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Device
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.model.request.CreateAccountReq
import tech.kotlin.model.request.LoginReq
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.service.account.AccountService
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check
import tech.kotlin.utils.exceptions.tryExec
import tech.kotlin.utils.ok

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountController {

    val login = Route { req, _ ->
        val resp = AccountService.loginWithName(LoginReq().apply {
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
            this.loginName = req.queryParams("login_name").check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
        })
        return@Route ok {
            it["uid"] = resp.userInfo.uid
            it["token"] = resp.token
            it["username"] = resp.userInfo.username
            it["email"] = resp.userInfo.email
            it["is_email_validate"] = resp.userInfo.emailState == UserInfo.EmailState.VERIFIED
        }
    }

    val register = Route { req, _ ->
        val resp = AccountService.create(CreateAccountReq().apply {
            this.username = req.queryParams("username").check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
            this.email = req.queryParams("email").check(Err.PARAMETER, "无效的邮箱") { !it.isNullOrBlank() }
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
        })
        return@Route ok {
            it["uid"] = resp.account.id
            it["token"] = resp.token
        }
    }

    val getUserInfo = Route { req, _ ->
        val query = req.params(":uid")
                .check(Err.PARAMETER, "非法的用户请求") { it.toLong(); true }
                .toLong()

        val checkTokenResp = TokenService.checkToken(CheckTokenReq(req))
        if (query != checkTokenResp.account.id
                && checkTokenResp.account.role != Account.Role.ADMIN)
            abort(Err.UNAUTHORIZED, "未授权访问该用户信息")

        val queryUser = UserService.queryById(QueryUserReq().apply { id = arrayListOf(query) })
        val info = queryUser.info[query] ?: abort(Err.SYSTEM)
        val account = queryUser.account[query] ?: abort(Err.SYSTEM)

        return@Route ok {
            it["username"] = info.username
            it["email"] = info.email
            it["is_email_validate"] = (info.emailState == UserInfo.EmailState.VERIFIED)
            it["last_login"] = account.lastLogin
            it["state"] = account.state
            it["role"] = account.role
            it["create_time"] = account.createTime
        }
    }
}