package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Device
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.*
import tech.kotlin.service.account.AccountService
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check
import tech.kotlin.utils.exceptions.tryExec

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
        val uid = req.params(":uid")
                .check(Err.PARAMETER, "uid错误") { it.toLong(); true }
                .toLong()

        val queryUser = UserService.queryById(QueryUserReq().apply { id = arrayListOf(uid) })
        val info = queryUser.info[uid] ?: abort(Err.USER_NOT_EXISTS)
        val account = queryUser.account[uid] ?: abort(Err.SYSTEM)

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

    val alterPassword = Route { req, _ ->
        val uid = req.params(":uid")
                .check(Err.PARAMETER, "uid错误") { it.toLong();true }
                .toLong()

        val password = req.queryParams("password")
                .check(Err.PARAMETER, "密码长度过短") { !it.isNullOrBlank() && it.length >= 8 }
        password.chars().forEach { it ->
            if ('a'.toInt() <= it && it <= 'z'.toInt()) return@forEach
            if ('A'.toInt() <= it && it <= 'Z'.toInt()) return@forEach
            if ('0'.toInt() <= it && it <= '9'.toInt()) return@forEach
            abort(Err.PARAMETER, "密码格式有误")
        }

        TokenService.checkToken(CheckTokenReq(req)).account
                .check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        AccountService.updatePassword(UpdatePasswordReq().apply {
            this.id = uid
            this.password = password
        })

        return@Route ok()
    }

    val updateUserInfo = Route { req, _ ->
        val uid = req.params(":uid")
                .check(Err.PARAMETER, "uid错误") { it.toLong();true }
                .toLong()

        val username = req.queryParams("username") ?: ""
        val email = req.queryParams("email") ?: ""
        val logo = req.queryParams("logo") ?: ""

        if (username.isNullOrBlank() && email.isNullOrBlank() && logo.isNullOrBlank())
            abort(Err.PARAMETER)

        TokenService.checkToken(CheckTokenReq(req)).account
                .check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        UserService.updateById(UpdateUserReq().apply {
            this.id = uid
            this.args = HashMap<String, String>().apply {
                if (!username.isNullOrBlank()) this["username"] = username
                if (!email.isNullOrBlank()) this["email"] = email
                if (!logo.isNullOrBlank()) this["logo"] = logo
            }
        })
        return@Route ok()
    }

}