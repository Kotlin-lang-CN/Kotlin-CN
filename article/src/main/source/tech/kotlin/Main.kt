package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.os.Log
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
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.Redis
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val properties = Props.loads("project.properties")

fun main(vararg args: String) {
    Redis.init(properties)
    Mysql.init(config = "mybatis.xml", properties = properties, sql = "init.sql")
    initRpcCgi(if (args.isNotEmpty()) args[0] else "")
    initHttpCgi(if (args.size >= 2) args[1] else "")
}

fun initRpcCgi(publishHost: String) {
    Serv.init(EtcdRegistrator(properties))
    Serv.register(ArticleApi::class, ArticleService)
    Serv.register(ReplyApi::class, ReplieService)
    Serv.register(TextApi::class, TextService)
    Serv.register(FlowerApi::class, FlowerService)
    val port = publishHost.tryExec(Err.SYSTEM, "illegal publish host $publishHost") { it.toInt() }
    Serv.publish(
            broadcastIp = properties str "deploy.broadcast.host", port = port,
            serviceName = ServDef.ARTICLE, executorService = Executors.newFixedThreadPool(20)
    )
}

fun initHttpCgi(cgiPort: String) {
    if (cgiPort.isNullOrBlank()) return
    Log.i("init cgi port @ $cgiPort")
    port(cgiPort.toInt())
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

        path("/misc") {
            get("/dashboard", MiscController.getDashboard.gate("网站公告栏"))
            post("/dashboard", MiscController.setDashboard.gate("设置网站公告"))
            get("/home/link", MiscController.getHomeLink.gate("首页链接"))
            post("/home/link", MiscController.setHomeLink.gate("设置首页链接"))
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