package tech.kotlin

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import spark.Spark.*
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.gate
import tech.kotlin.common.utils.str
import tech.kotlin.common.utils.tryExec
import tech.kotlin.controller.MessageController
import tech.kotlin.service.Err
import tech.kotlin.service.MessageService
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.*
import tech.kotlin.service.message.MessageApi
import java.util.concurrent.Executors

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
    var http: Int = 8082

    @Parameter(names = arrayOf("-p", "--publish"),
               required = false,
               description = "服务发布端口")
    var publish: Int = 9002
}

fun main(args: Array<String>) {
    JCommander.newBuilder()
            .addObject(Launcher)
            .programName(ServDef.ACCOUNT)
            .build()
            .parse(*args)

    Log.i("Launcher", Json.dumps(Launcher))

    Log.LOG_LEVEL = Launcher.log
    Props.init(Launcher.config)
    Redis.init(Props)
    initService()
    initHttpCgi()
}

fun initService() {
    Serv.init(EtcdRegistrator(Props))
    Serv.register(MessageApi::class, MessageService)
    Serv.publish(broadcastIp = Props str "deploy.broadcast.host",
                 port = Launcher.publish,
                 serviceName = ServDef.MESSAGE,
                 executorService = Executors.newFixedThreadPool(20))
}

fun initHttpCgi() {
    port(Launcher.http)
    init()
    path("/api") {
        path("/message") {
            get("/latest", MessageController.unread.gate("获取用户消息列表"))
            post("/read/:id", MessageController.markRead.gate("标记已读"))

            get("/article/subscribe/count", MessageController.articleSubscriberCount.gate("文章订阅数"))
            get("/article/:id/subscriber", MessageController.articleSubscriber.gate("文章订阅者"))
            get("/article/:id/subscribe", MessageController.subscribeState.gate("我的订阅状态"))
            post("/article/:id/subscribe", MessageController.subscribeArticle.gate("订阅文章"))
            post("/article/:id/unsubscribe", MessageController.unsubscribeArticle.gate("取消订阅文章"))
        }
    }
}