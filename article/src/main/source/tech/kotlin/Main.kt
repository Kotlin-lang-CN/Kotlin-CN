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
import tech.kotlin.service.*
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.FlowerApi
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.service.article.TextApi
import tech.kotlin.common.mysql.Mysql
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
    var http: Int = 8080

    @Parameter(names = arrayOf("-p", "--publish"),
               required = false,
               description = "服务发布端口")
    var publish: Int = 9000
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
    Mysql.init(config = "mybatis.xml", properties = Props, sql = "init.sql")

    initService()
    initHttpServer()
}

fun initService() {
    Serv.init(EtcdRegistrator(Props))
    Serv.register(ArticleApi::class, ArticleService)
    Serv.register(ReplyApi::class, ReplieService)
    Serv.register(TextApi::class, TextService)
    Serv.register(FlowerApi::class, FlowerService)
    Serv.publish(
            broadcastIp = Props str "deploy.broadcast.host", port = Launcher.publish,
            serviceName = ServDef.ARTICLE, executorService = Executors.newFixedThreadPool(20)
                )
}

fun initHttpServer() {
    port(Launcher.http)
    init()
    path("/api") {
        path("/article") {
            post("/post", ArticleController.postArticle.gate("发布文章"))
            post("/post/:id/update", ArticleController.updateArticle.gate("更新文章"))
            post("/post/:id/delete", ArticleController.deleteArticle.gate("删除文章"))
            get("/post/:id", ArticleController.getArticleById.gate("获取文章详细内容"))

            get("/list", ArticleViewController.getList.gate("获取最新文章列表"))
            get("/fine", ArticleViewController.getFine.gate("获取精品文章"))
            get("/category/:id", ArticleViewController.getByCategory.gate("根据类型获取最新文章列表"))
            get("/category", ArticleViewController.getCategory.gate("获取文章类型列表"))

            get("/:id/reply", ReplyController.queryReply.gate("获取文章评论列表"))
            post("/:id/reply", ReplyController.createReply.gate("参与文章评论"))
            post("/reply/:id/delete", ReplyController.delReply.gate("删除评论"))
            get("/reply/count", ReplyController.queryReplyCount.gate("获取文章评论数量"))
        }

        path("/flower") {
            post("/article/:id/star", FlowerController.starArticle.gate("点赞文章"))
            post("/article/:id/unstar", FlowerController.unstarArticle.gate("取消点赞文章"))
            get("/article/:id/star", FlowerController.queryArticle.gate("获取对文章的点赞状态"))
            get("/article/star/count", FlowerController.countArticle.gate("获取文章点赞数量"))

            post("/reply/:id/star", FlowerController.starReply.gate("点赞评论"))
            post("/reply/:id/unstar", FlowerController.unstarReply.gate("取消点赞评论"))
            get("/reply/:id/star", FlowerController.queryReply.gate("获取对评论的点赞状态"))
            get("/reply/star/count", FlowerController.countReply.gate("获取评论点赞数量"))
        }

        path("/rss") {
            get("/fine", RssController.fine)
            get("/latest", RssController.latest)
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