package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.domain.Device
import tech.kotlin.common.utils.ok
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.Err
import tech.kotlin.common.utils.check
import tech.kotlin.service.account.GithubApi
import tech.kotlin.service.account.req.*
import tech.kotlin.service.domain.UserInfo

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubController {

    val githubApi by Serv.bind(GithubApi::class)
    val sessionApi by Serv.bind(SessionApi::class)

    val createState = Route { req, _ ->
        val resp = githubApi.createState(GithubCreateStateReq().apply {
            this.device = Device(req)
        })
        return@Route ok { it["state"] = resp.state }
    }

    val auth = Route { req, _ ->
        val device = Device(req)
        val code = req.queryParams("code").check(Err.GITHUB_AUTH_ERR, "缺少code") { !it.isNullOrBlank() }
        val state = req.queryParams("state").check(Err.GITHUB_AUTH_ERR, "缺少state") { !it.isNullOrBlank() }
        val authResp = githubApi.createSession(GithubAuthReq().apply {
            this.device = device
            this.code = code
            this.state = state
        })
        if (!authResp.hasAccount) {
            return@Route ok {
                it["need_create_account"] = true
                it["github_token"] = authResp.token
                it["github_email"] = authResp.github.email
                it["github_name"] = authResp.github.name
                it["github_avatar"] = authResp.github.avatar
            }
        } else {
            val token = sessionApi.createSession(CreateSessionReq().apply {
                this.device = device
                this.uid = authResp.account.id
            }).token
            return@Route ok {
                it["need_create_account"] = false
                it["uid"] = authResp.account.id
                it["token"] = token
                it["username"] = authResp.userInfo.username
                it["email"] = authResp.userInfo.email
                it["is_email_validate"] = authResp.userInfo.emailState == UserInfo.EmailState.VERIFIED
                it["role"] = authResp.account.role
            }
        }
    }

}