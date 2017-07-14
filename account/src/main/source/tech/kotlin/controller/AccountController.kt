package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.algorithm.MD5
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.*
import tech.kotlin.service.*
import tech.kotlin.service.account.req.*
import tech.kotlin.service.account.resp.GithubCheckTokenReq
import tech.kotlin.service.article.req.EmailCheckTokenReq
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.Device
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.message.MessageApi
import java.net.URLDecoder

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountController {

    val login = Route { req, _ ->
        val githubToken = req.queryParams("github_token")

        val loginReq = LoginReq().apply {
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }

            this.loginName = req.queryParams("login_name")
                    .check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() }

            this.password = req.queryParams("password")
                    .check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() }

            if (!githubToken.isNullOrBlank()) {
                this.githubUser = GithubService.checkToken(GithubCheckTokenReq().apply {
                    this.token = githubToken
                    this.device = Device(req)
                }).info
            }
        }

        val loginResp = AccountService.loginWithName(loginReq)
        if (!loginReq.githubUser.avatar.isNullOrBlank() && loginResp.userInfo.logo.isNullOrBlank()) {
            UserService.updateById(UpdateUserReq().apply {
                this.id = loginResp.account.id
                this.args = strDict { this["logo"] = loginReq.githubUser.avatar }
            })
        }
        val logo = if (loginReq.githubUser.avatar.isNullOrBlank())
            loginResp.userInfo.logo else loginReq.githubUser.avatar

        return@Route ok {
            it["uid"] = loginResp.userInfo.uid
            it["token"] = loginResp.token
            it["username"] = loginResp.userInfo.username
            it["email"] = loginResp.userInfo.email
            it["is_email_validate"] = loginResp.userInfo.emailState == UserInfo.EmailState.VERIFIED
            it["role"] = loginResp.account.role
            it["logo"] = logo
        }
    }

    val register = Route { req, _ ->
        val username = req.queryParams("username")
                .check(Err.PARAMETER, "无效的用户名") { !it.isNullOrBlank() && it.trim().length >= 2 }
        val password = req.queryParams("password")
                .check(Err.PARAMETER, "无效的密码") { !it.isNullOrBlank() && it.length >= 8 }
        val email = req.queryParams("email")
                .check(Err.PARAMETER, "无效的邮箱") {
                    !it.isNullOrBlank() && it.matches(Regex(
                            "^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"))
                }

        val githubToken = req.queryParams("github_token")

        //创建账号
        val createResp = AccountService.create(CreateAccountReq().apply {
            this.username = username
            this.password = password
            this.email = email
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
            if (!githubToken.isNullOrBlank()) {
                this.githubUser = GithubService.checkToken(GithubCheckTokenReq().apply {
                    this.token = githubToken
                    this.device = Device(req)
                }).info
            }
        })

        //修改头像
        val logo = req.queryParams("logo")
        if (!logo.isNullOrBlank()) {
            UserService.updateById(UpdateUserReq().apply {
                this.id = createResp.account.id
                this.args = strDict { this["logo"] = logo }
            })
        }

        return@Route ok {
            it["uid"] = createResp.account.id
            it["token"] = createResp.token
            it["logo"] = logo ?: createResp.userInfo.logo
        }
    }

    val activateEmail = Route { req, _ ->
        val token = URLDecoder.decode(req.queryParams("token"), "UTF-8")
        val resp = EmailService.checkToken(EmailCheckTokenReq().apply { this.token = token })
        UserService.activateEmail(ActivateEmailReq().apply { this.uid = resp.uid;this.email = resp.email })
        return@Route ok()
    }

    val getUserInfo = Route { req, _ ->
        val uid = req.params(":uid")
                .check(Err.PARAMETER, "uid错误") { it.toLong(); true }
                .toLong()

        val owner = SessionService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN || it.id == uid }

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
            it["logo"] = info.logo
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

        SessionService.checkToken(CheckTokenReq(req)).account
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

        SessionService.checkToken(CheckTokenReq(req)).account
                .check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        UserService.updateById(UpdateUserReq().apply {
            this.id = uid
            this.args = strDict {
                if (!username.isNullOrBlank()) this["username"] = username
                if (!email.isNullOrBlank()) this["email"] = email
                if (!logo.isNullOrBlank()) this["logo"] = logo
            }
        })
        return@Route ok()
    }

}