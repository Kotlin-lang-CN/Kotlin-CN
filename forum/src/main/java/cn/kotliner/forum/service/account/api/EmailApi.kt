package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.CreateEmailSessionReq
import cn.kotliner.forum.service.account.resp.CreateEmailSessionResp
import cn.kotliner.forum.service.account.resp.EmailCheckTokenResp
import cn.kotliner.forum.service.article.req.EmailCheckTokenReq
import cn.kotliner.forum.service.article.req.EmailReq


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface EmailApi {

    fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp

    fun checkToken(req: EmailCheckTokenReq): EmailCheckTokenResp

    fun send(req: EmailReq)

}