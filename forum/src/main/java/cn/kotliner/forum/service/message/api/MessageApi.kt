package cn.kotliner.forum.service.message.api

import cn.kotliner.forum.service.message.req.*
import cn.kotliner.forum.service.message.resp.QueryMessageResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface MessageApi {

    fun broadCast(req: BroadcastReq)

    fun listCast(req: ListcastReq)

    fun groupCast(req: GroupcastReq)

    fun getByAcceptor(req: QueryMessageReq): QueryMessageResp

    fun markReadReq(req: MarkReadReq)

}