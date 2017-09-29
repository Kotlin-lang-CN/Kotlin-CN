package cn.kotliner.forum.service.account.api

import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.account.req.UpdateProfileReq
import cn.kotliner.forum.service.account.resp.QueryProfileResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ProfileApi {

    fun queryById(req: QueryUserReq): QueryProfileResp

    fun updateById(req: UpdateProfileReq)

}