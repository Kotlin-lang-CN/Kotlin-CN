package cn.kotliner.forum.service.message.api

import cn.kotliner.forum.service.message.req.CountGroupReq
import cn.kotliner.forum.service.message.resp.CountGroupResp
import cn.kotliner.forum.service.message.req.GroupReq
import cn.kotliner.forum.service.message.req.ListGroupReq
import cn.kotliner.forum.service.message.resp.ListGroupResp
import cn.kotliner.forum.service.message.resp.QueryGroupStateResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface GroupApi {

    fun queryGroupState(req: GroupReq): QueryGroupStateResp

    fun joinGroup(req: GroupReq)

    fun leaveGroup(req: GroupReq)

    fun listGroup(req: ListGroupReq): ListGroupResp

    fun countGroup(req: CountGroupReq): CountGroupResp


}

