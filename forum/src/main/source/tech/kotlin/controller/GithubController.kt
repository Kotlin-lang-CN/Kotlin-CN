package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Device
import tech.kotlin.model.request.CreateAuthSessionReq
import tech.kotlin.model.request.CreateSessionReq
import tech.kotlin.model.request.GithubAuthReq
import tech.kotlin.service.account.GithubService
import tech.kotlin.service.account.TokenService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubController {

    val startAuth = Route { req, _ ->
        val resp = GithubService.createAuthSession(CreateAuthSessionReq().apply {
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
        val authResp = GithubService.handleAuthCallback(GithubAuthReq().apply {
            this.device = device
            this.code = code
            this.state = state
        })
        if (!authResp.hasAccount) {

        } else {
            val token = TokenService.createSession(CreateSessionReq().apply {
                this.uid = authResp.account.id
                this.device = device
            }).token
            resp.cookie("X-App-UID", "${authResp.account.id}")
            resp.cookie("X-App-Token", token)
        }
        return@Route ok {

        }
    }

}