package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.CheckTokenReq
import cn.kotliner.forum.service.account.req.CreateSessionReq
import cn.kotliner.forum.service.account.resp.CheckTokenResp
import cn.kotliner.forum.service.account.resp.CreateSessionResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface SessionApi {

    fun createSession(req: CreateSessionReq): CreateSessionResp

    fun checkToken(req: CheckTokenReq): CheckTokenResp

}