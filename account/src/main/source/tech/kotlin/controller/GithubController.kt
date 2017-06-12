package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.model.domain.Device
import tech.kotlin.model.request.CreateAuthSessionReq
import tech.kotlin.model.request.CreateSessionReq
import tech.kotlin.model.request.GithubAuthReq
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ServDef
import tech.kotlin.service.Githubs
import tech.kotlin.service.account.SessionApi
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubController {

    val sessionApi by Serv.bind(SessionApi::class)

    val startAuth = Route { req, _ ->
        val resp = Githubs.createAuthSession(CreateAuthSessionReq().apply {
            this.device = Device(req)
        })
        return@Route ok {
            it["state"] = resp.state
        }
    }

    val authCallback = Route { req, resp ->
        val device = Device(req)
        val code = req.queryParams("code").check(Err.GITHUB_AUTH_ERR) { !it.isNullOrBlank() }
        val state = req.queryParams("state").check(Err.GITHUB_AUTH_ERR) { !it.isNullOrBlank() }
        val authResp = Githubs.handleAuthCallback(GithubAuthReq().apply {
            this.device = device
            this.code = code
            this.state = state
        })
        if (!authResp.hasAccount) {
            return@Route ok {
                it["need_create_account"] = true
            }
        } else {
            val token = sessionApi.createSession(CreateSessionReq().apply {
                this.uid = authResp.account.id
                this.device = device
            }).token
            resp.cookie("X-App-UID", "${authResp.account.id}")
            resp.cookie("X-App-Token", token)
            return@Route ok {

            }
        }
    }

}