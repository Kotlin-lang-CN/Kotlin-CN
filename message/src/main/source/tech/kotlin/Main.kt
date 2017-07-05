package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.str
import tech.kotlin.common.utils.tryExec
import tech.kotlin.controller.MessageController
import tech.kotlin.service.Err
import tech.kotlin.service.ServDef
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/

fun main(args: Array<String>) {
    Redis.init(Props)
    Mysql.init(config = "mybatis.xml", properties = Props, sql = "init.sql")
    initRpcCgi(if (args.isNotEmpty()) args[0] else "")
    initHttpCgi(if (args.size >= 2) args[1] else "")
}

fun initRpcCgi(cgiPort: String) {
    Serv.init(EtcdRegistrator(Props))
    val port = cgiPort.tryExec(Err.SYSTEM, "illegal publish host $cgiPort") { it.toInt() }
    Serv.publish(
            broadcastIp = Props str "deploy.broadcast.host", port = port,
            serviceName = ServDef.ARTICLE, executorService = Executors.newFixedThreadPool(20)
    )
}

fun initHttpCgi(cgiPort: String) {
    if (cgiPort.isNullOrBlank()) return
    Log.i("init cgi port @ $cgiPort")
    port(cgiPort.toInt())
    init()
    path("/api") {
        path("/message") {
            get("/latest", MessageController.unread)
            post("/read/:id", MessageController.markRead)
            post("/read/all", MessageController.markReadAll)
        }
    }
}