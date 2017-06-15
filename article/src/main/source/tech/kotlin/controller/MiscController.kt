package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.ok
import tech.kotlin.service.ServDef
import tech.kotlin.service.TypeDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.domain.Account
import tech.kotlin.utils.Redis

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MiscController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)

    //获取网站通告
    val getDashboard = Route { _, _ -> return@Route ok { it["text"] = Redis.read { it["dashboard"] } ?: "" } }

    //设置网站通告
    val setDashboard = Route { req, _ ->
        val dashboard = req.queryParams("dashboard") ?: ""
        sessionApi.checkToken(CheckTokenReq(req)).account.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }
        Redis.write { it["dashboard"] = dashboard }
        return@Route ok()
    }

    //首页链接
    val getHomeLink = Route { _, _ ->
        return@Route ok { it["link"] = Redis.read { it["link"] ?: "" } }
    }

    //设置首页链接
    val setHomeLink = Route { req, _ ->
        val dashboard = req.queryParams("link") ?: ""
        sessionApi.checkToken(CheckTokenReq(req)).account.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }
        Redis.write { it["link"] = dashboard }
        return@Route ok()
    }
}
