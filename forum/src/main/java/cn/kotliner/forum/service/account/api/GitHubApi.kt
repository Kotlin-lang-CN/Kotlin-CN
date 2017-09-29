package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.GithubAuthReq
import cn.kotliner.forum.service.account.req.GithubCreateStateReq
import cn.kotliner.forum.service.account.resp.GithubAuthResp
import cn.kotliner.forum.service.account.resp.GithubCheckTokenReq
import cn.kotliner.forum.service.account.resp.GithubCheckTokenResp
import cn.kotliner.forum.service.account.resp.GithubCreateStateResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface GitHubApi {

    fun createState(req: GithubCreateStateReq): GithubCreateStateResp

    fun createSession(req: GithubAuthReq): GithubAuthResp

    fun checkToken(req: GithubCheckTokenReq): GithubCheckTokenResp

}


