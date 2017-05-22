package tech.kotlin.account.controller

import spark.Route
import tech.kotlin.account.API.ACCOUNT
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.exceptions.check
import tech.kotlin.common.exceptions.tryExec
import tech.kotlin.model.Account
import tech.kotlin.model.Device
import tech.kotlin.model.UserInfo
import tech.kotlin.service.Node
import tech.kotlin.service.account.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountController {

    val accountService: AccountService by lazy { Node[ACCOUNT][AccountService::class.java] }
    val tokenService: TokenService by lazy { Node[ACCOUNT][TokenService::class.java] }
    val userService: UserService by lazy { Node[ACCOUNT][UserService::class.java] }

    val login = Route { req, response ->
        val resp = accountService.loginWithName(LoginReq().apply {
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
            this.loginName = req.queryParams("login_name").check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
        })

        response.cookie("X-App-Token", resp.token)
        return@Route HashMap<String, Any>().apply {
            this["uid"] = resp.userInfo.uid
            this["token"] = resp.token
            this["username"] = resp.userInfo.username
            this["email"] = resp.userInfo.email
            this["is_email_validate"] = resp.userInfo.emailState == UserInfo.EmailState.VERIFIED
        }
    }

    val register = Route { req, response ->
        val resp = accountService.create(CreateAccountReq().apply {
            this.username = req.queryParams("username").check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
            this.email = req.queryParams("email").check(Err.PARAMETER, "无效的邮箱") { !it.isNullOrBlank() }
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
        })

        response.cookie("X-App-Token", resp.token)
        return@Route HashMap<String, Any>().apply {
            this["uid"] = resp.account.id
            this["token"] = resp.token
            this["username"] = resp.userInfo.username
            this["email"] = resp.userInfo.email
            this["is_email_validate"] = (resp.userInfo.emailState == UserInfo.EmailState.VERIFIED)
        }
    }

    val getUserInfo = Route { req, _ ->
        val query = req.params(":uid")
                .check(Err.PARAMETER, "非法的用户请求") { it.toLong(); true }
                .toLong()

        val checkTokenResp = tokenService.checkToken(CheckTokenReq(req))
        if (query != checkTokenResp.account.id
                && checkTokenResp.account.role != Account.Role.ADMIN)
            abort(Err.UNAUTHORIZED, "未授权访问该用户信息")

        val queryUser = userService.queryById(QueryUserReq().apply { id = arrayListOf(query) })
        val info = queryUser.info[query] ?: abort(Err.SYSTEM)
        val account = queryUser.account[query] ?: abort(Err.SYSTEM)

        return@Route HashMap<String, Any>().apply {
            this["username"] = info.username
            this["email"] = info.email
            this["is_email_validate"] = (info.emailState == UserInfo.EmailState.VERIFIED)
            this["last_login"] = account.lastLogin
            this["state"] = account.state
            this["role"] = account.role
            this["create_time"] = account.createTime
        }
    }
}