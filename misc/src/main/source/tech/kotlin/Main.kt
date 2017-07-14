package tech.kotlin

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import spark.Spark.*
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.controller.*
import tech.kotlin.service.ServDef

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Launcher {
    @Parameter(names = arrayOf("-c", "--config-file"),
               required = false,
               description = "指定配置文件")
    var config: String = ""

    @Parameter(names = arrayOf("-l", "--log-level"),
               required = false,
               description = "指定日志等级")
    var log: Int = Log.LOG_LEVEL

    @Parameter(names = arrayOf("-h", "--http-port"),
               required = false,
               description = "http服务端口号")
    var http: Int = 8083
}

fun main(vararg args: String) {
    JCommander.newBuilder()
            .addObject(Launcher)
            .programName(ServDef.ACCOUNT)
            .build()
            .parse(*args)

    Log.i("Launcher", Json.dumps(Launcher))

    Log.LOG_LEVEL = Launcher.log
    Props.init(Launcher.config)
    Redis.init(Props)
    Serv.init(EtcdRegistrator(Props))
    initHttpCgi()
}

fun initHttpCgi() {
    port(Launcher.http)
    init()
    path("/api") {
        path("/misc") {
            get("/dashboard", MiscController.getDashboard.gate("网站公告栏"))
            post("/dashboard", MiscController.setDashboard.gate("设置网站公告"))
            get("/home/link", MiscController.getHomeLink.gate("首页链接"))
            post("/home/link", MiscController.setHomeLink.gate("设置首页链接"))
        }

        path("/file") {
            get("/token", FileController.qiniuToken.gate("获取qiniu上传token"))
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