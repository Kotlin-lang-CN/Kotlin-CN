package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.PropRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.dict
import tech.kotlin.common.utils.gate
import tech.kotlin.common.utils.int
import tech.kotlin.controller.*
import tech.kotlin.service.Articles
import tech.kotlin.service.Replies
import tech.kotlin.service.ServDef
import tech.kotlin.service.Texts
import tech.kotlin.service.article.ArticleApi
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.service.article.TextApi
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.Redis
import java.net.InetSocketAddress
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val properties = Props.loads("project.properties")

fun main(vararg args: String) {
    Redis.init(properties)
    Mysql.init(config = "mybatis.xml", properties = properties, sql = "init.sql")
    initRpcCgi()
    initHttpCgi()
}

fun initRpcCgi() {
    Serv.init(PropRegistrator(properties))
    Serv.register(ArticleApi::class, Articles)
    Serv.register(ReplyApi::class, Replies)
    Serv.register(TextApi::class, Texts)
    Serv.publish(
            address = InetSocketAddress("0.0.0.0", properties int "deploy.service.${ServDef.ARTICLE}.rpc"),
            serviceName = ServDef.ARTICLE,
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    )
}

fun initHttpCgi() {
    port(properties int "deploy.service.${ServDef.ARTICLE}.cgi")
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

        path("/rss") {
            get("/fine", RssController.fine)
            get("/latest", RssController.latest)
        }

        path("/misc") {
            get("/dashboard", MiscController.getDashboard.gate("网站公告栏"))
            post("/dashboard", MiscController.setDashboard.gate("设置网站公告"))
            get("/home/link", MiscController.getHomeLink.gate("首页链接"))
            post("/home/link", MiscController.setHomeLink.gate("设置首页链接"))
        }
    }
    notFound { req, response ->
        response.header("Access-Control-Allow-Origin", "*")
        response.header("Access-Control-Allow-Credentials", "true")
        response.header("Access-Control-Allow-Headers",
                "X-App-Device, X-App-Token, X-App-Platform, X-App-System, X-App-UID, X-App-Vendor")
        response.header("Access-Control-Allow-Methods", "GET, POST")
        if (req.requestMethod().toUpperCase() != "OPTIONS") {
            response.status(404)
            Json.dumps(dict { this["code"] = 404; this["msg"] = "not found" })
        } else {
            response.status(200)
            Json.dumps(dict { this["code"] = 0; this["msg"] = "" })
        }
    }
}