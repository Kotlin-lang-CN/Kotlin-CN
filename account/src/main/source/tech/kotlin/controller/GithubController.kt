package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.domain.Device
import tech.kotlin.service.account.req.GithubCreateStateReq
import tech.kotlin.service.account.req.CreateSessionReq
import tech.kotlin.service.account.req.GithubAuthReq
import tech.kotlin.common.utils.ok
import tech.kotlin.service.Githubs
import tech.kotlin.service.account.SessionApi
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.check
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.domain.UserInfo

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubController {

    val sessionApi by Serv.bind(SessionApi::class)
    val userApi by Serv.bind(UserApi::class)

    val createState = Route { req, _ ->
        val resp = Githubs.createState(GithubCreateStateReq().apply {
            this.device = Device(req)
        })
        return@Route ok { it["state"] = resp.state }
    }

    val auth = Route { req, _ ->
        val device = Device(req)
        val code = req.queryParams("code").check(Err.GITHUB_AUTH_ERR) { !it.isNullOrBlank() }
        val state = req.queryParams("state").check(Err.GITHUB_AUTH_ERR) { !it.isNullOrBlank() }
        val authResp = Githubs.createSession(GithubAuthReq().apply {
            this.device = device
            this.code = code
            this.state = state
        })
        if (!authResp.hasAccount) {
            return@Route ok {
                it["need_create_account"] = true
                it["github_token"] = authResp.token
            }
        } else {
            val token = sessionApi.createSession(CreateSessionReq().apply {
                this.uid = authResp.account.id
                this.device = device
            }).token
            val meta = userApi.queryById(QueryUserReq().apply {
                this.id = arrayListOf(authResp.account.id)
            })
            return@Route ok {
                it["need_create_account"] = false
                it["github_token"] = authResp.token
                it["uid"] = authResp.account.id
                it["token"] = token
                it["username"] = meta.info[authResp.account.id]?.username ?: ""
                it["email"] = meta.info[authResp.account.id]?.email ?: ""
                it["is_email_validate"] = meta.info[authResp.account.id]?.emailState == UserInfo.EmailState.VERIFIED
                it["role"] = meta.account[authResp.account.id]?.role ?: 0
            }
        }
    }

}