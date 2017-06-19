package tech.kotlin.service.article

import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.service.TypeDef
import tech.kotlin.service.article.req.FlowerReq
import tech.kotlin.service.article.resp.CountStarResp
import tech.kotlin.service.domain.EmptyResp


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface FlowerApi {

    @RpcInterface(TypeDef.Flower.STAR)
    fun star(req: FlowerReq): EmptyResp

    @RpcInterface(TypeDef.Flower.UNSTAR)
    fun unstar(req: FlowerReq): EmptyResp

    @RpcInterface(TypeDef.Flower.COUNT_STAR)
    fun countStar(req: FlowerReq): CountStarResp

}


