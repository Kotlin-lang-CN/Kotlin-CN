package tech.kotlin.account.controller

import spark.Route
import tech.kotlin.account.API.ACCOUNT
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.exceptions.require
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

    val login = Route { req, _ ->
        val resp = accountService.loginWithName(LoginReq().apply {
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
            this.loginName = req.queryParams("login_name").require(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").require(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
        })

        return@Route HashMap<String, Any>().apply {
            this += "uid" to resp.userInfo.uid
            this += "token" to resp.token
            this += "username" to resp.userInfo.username
            this += "email" to resp.userInfo.email
            this += "is_email_validate" to (resp.userInfo.emailState == UserInfo.EmailState.VERIFIED)
        }
    }

    val register = Route { req, _ ->
        val resp = accountService.create(CreateAccountReq().apply {
            this.username = req.queryParams("username").require(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }
            this.password = req.queryParams("password").require(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }
            this.email = req.queryParams("email").require(Err.PARAMETER, "无效的邮箱") { !it.isNullOrBlank() }
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
        })

        return@Route HashMap<String, Any>().apply {
            this += "uid" to resp.account.id
            this += "token" to resp.token
            this += "username" to resp.userInfo.username
            this += "email" to resp.userInfo.email
            this += "is_email_validate" to (resp.userInfo.emailState == UserInfo.EmailState.VERIFIED)
        }
    }

    val getUserInfo = Route { req, _ ->
        val query = req.params(":uid")
                .require(Err.PARAMETER, "非法的用户请求") { it.toLong(); true }
                .toLong()

        val checkTokenResp = tokenService.checkToken(CheckTokenReq(req))
        if (query != checkTokenResp.account.id
                && checkTokenResp.account.role != Account.Role.ADMIN)
            abort(Err.UNAUTHORIZED, "未授权访问该用户信息")

        val queryUser = userService.queryById(QueryUserReq().apply { id = arrayListOf(query) })
        val info = queryUser.info[query] ?: abort(Err.SYSTEM)
        val account = queryUser.account[query] ?: abort(Err.SYSTEM)

        return@Route HashMap<String, Any>().apply {
            this += "username" to info.username
            this += "email" to info.email
            this += "is_email_validate" to (info.emailState == UserInfo.EmailState.VERIFIED)
            this += "last_login" to account.lastLogin
            this += "state" to account.state
            this += "role" to account.role
            this += "create_time" to account.createTime
        }
    }
}