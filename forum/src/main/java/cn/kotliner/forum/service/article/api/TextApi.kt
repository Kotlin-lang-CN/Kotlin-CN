package cn.kotliner.forum.service.article.api

import cn.kotliner.forum.service.article.req.CreateTextContentReq
import cn.kotliner.forum.service.article.req.QueryTextReq
import cn.kotliner.forum.service.article.resp.CreateTextContentResp
import cn.kotliner.forum.service.article.resp.QueryTextResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface TextApi {

    fun getById(req: QueryTextReq): QueryTextResp

    fun createContent(req: CreateTextContentReq): CreateTextContentResp

}