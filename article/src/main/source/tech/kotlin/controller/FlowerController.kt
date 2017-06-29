package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.ok
import tech.kotlin.common.utils.tryExec
import tech.kotlin.service.Err
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.article.FlowerApi
import tech.kotlin.service.article.req.CountStarReq
import tech.kotlin.service.article.req.StarReq

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object FlowerController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)
    val flowerApi by Serv.bind(FlowerApi::class)

    val starArticle = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "缺少article") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        flowerApi.star(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok()
    }

    val unstarArticle = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "缺少article") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        flowerApi.unstar(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok()
    }

    val queryArticle = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "缺少article") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        val data = flowerApi.queryStar(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok {
            it["has_star"] = data.hasStar
            it["flower"] = data.flower
        }
    }

    val countArticle = Route { req, _ ->
        val ids = req.tryExec(Err.PARAMETER, "非法的id信息") {
            it.queryParams("ids").split(",").filter { it.isNotBlank() }.map { it.trim().toLong() }.map { "article:$it" }
        }
        val resp = flowerApi.countStar(CountStarReq().apply { this.poolIds = ids })
        return@Route ok {
            it["data"] = resp.result.entries.map { it.key.split(":")[1] to it.value }.toMap()
        }
    }

    val starReply = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "非法的article id") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        flowerApi.star(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok()
    }

    val unstarReply = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "非法的article id") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        flowerApi.unstar(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok()
    }

    val queryReply = Route { req, _ ->
        val articleId = req.tryExec(Err.PARAMETER, "非法的reply id") { it.params(":id").toLong() }
        val account = sessionApi.checkToken(CheckTokenReq(req)).account
        val data = flowerApi.queryStar(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return@Route ok {
            it["has_star"] = data.hasStar
            it["flower"] = data.flower
        }
    }

    val countReply = Route { req, _ ->
        val ids = req.tryExec(Err.PARAMETER, "非法的reply id") {
            it.queryParams("ids").split(",").filter { it.isNotBlank() }.map { it.trim().toLong() }.map { "reply:$it" }
        }
        val resp = flowerApi.countStar(CountStarReq().apply { this.poolIds = ids })
        return@Route ok {
            it["data"] = resp.result.entries.map { it.key.split(":")[1] to it.value }.toMap()
        }
    }
}