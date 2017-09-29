package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.ActivateEmailReq
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.account.req.UpdateUserReq
import cn.kotliner.forum.service.account.resp.QueryUserResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface UserApi {

    fun queryById(req: QueryUserReq): QueryUserResp

    fun updateById(req: UpdateUserReq)

    fun activateEmail(req: ActivateEmailReq)

}