package cn.kotliner.forum.service.article.api

import cn.kotliner.forum.service.article.req.CountStarReq
import cn.kotliner.forum.service.article.req.StarReq
import cn.kotliner.forum.service.article.resp.CountStarResp
import cn.kotliner.forum.service.article.resp.QueryStarResp


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface FlowerApi {

    fun star(req: StarReq)

    fun unstar(req: StarReq)

    fun countStar(req: CountStarReq): CountStarResp

    fun queryStar(req: StarReq): QueryStarResp

}
