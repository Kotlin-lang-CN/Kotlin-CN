package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.controller.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val properties = Props.loads("project.properties")

fun main(vararg args: String) {
    Redis.init(properties)
    Serv.init(EtcdRegistrator(properties))
    initHttpCgi(if (args.size >= 2) args[1] else "")
}

fun initHttpCgi(cgiPort: String) {
    if (cgiPort.isNullOrBlank()) return
    Log.i("init cgi port @ $cgiPort")
    port(cgiPort.toInt())
    init()
    path("/api") {
        path("/misc") {
            get("/dashboard", MiscController.getDashboard.gate("网站公告栏"))
            post("/dashboard", MiscController.setDashboard.gate("设置网站公告"))
            get("/home/link", MiscController.getHomeLink.gate("首页链接"))
            post("/home/link", MiscController.setHomeLink.gate("设置首页链接"))
        }
        path("/file") {
            get("/token", FileController.getLogaToken.gate("获取qiniu上传token"))
        }
    }
    notFound { req, response ->
        if (req.requestMethod().toUpperCase() != "OPTIONS") {
            response.status(404)
            Json.dumps(dict { this["code"] = 404; this["msg"] = "not found" })
        } else {
            response.status(200)
            Json.dumps(dict { this["code"] = 0; this["msg"] = "" })
        }
    }
}