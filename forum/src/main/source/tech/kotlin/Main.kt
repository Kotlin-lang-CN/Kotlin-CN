package tech.kotlin

import spark.Route
import spark.Spark
import tech.kotlin.controller.*
import tech.kotlin.utils.exceptions.Abort
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.log.Log
import tech.kotlin.utils.mysql.Mysql
import tech.kotlin.utils.properties.Props
import tech.kotlin.utils.properties.int
import tech.kotlin.utils.redis.Redis
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val properties = Props.loads("project.properties")

fun main(vararg args: String) {
    Redis.init(properties)
    Mysql.init(config = "mybatis.xml", properties = properties, sql = "init.sql")

    Spark.port(properties int "deploy.port")
    Spark.init()
    Spark.path("/api") {
        Spark.path("/account") {
            Spark.post("/login", AccountController.login.gate("用户登录"))
            Spark.post("/register", AccountController.register.gate("用户注册"))
            Spark.post("/user/:uid/password", AccountController.alterPassword.gate("修改密码"))
            Spark.post("/user/:uid/update", AccountController.updateUserInfo.gate("更新用户信息"))
            Spark.get("/user/:uid", AccountController.getUserInfo.gate("查询用户信息"))
        }

        Spark.path("/github") {
            Spark.get("/oauth", GithubController.startAuth.gate("获取登录会话"))
            Spark.get("/callback", GithubController.authCallback.gate("github第三方回调"))
        }

        Spark.path("/article") {
            Spark.post("/post", ArticleController.postArticle.gate("发布文章"))
            Spark.post("/post/:id/update", ArticleController.updateArticle.gate("更新文章"))
            Spark.post("/post/:id/delete", ArticleController.deleteArticle.gate("删除文章"))
            Spark.get("/post/:id", ArticleController.getArticleById.gate("获取文章详细内容"))

            Spark.get("/list", ArticleViewController.getList.gate("获取最新文章列表"))
            Spark.get("/fine", ArticleViewController.getFine.gate("获取精品文章"))
            Spark.get("/category/:id", ArticleViewController.getByCategory.gate("根据类型获取最新文章列表"))

            Spark.get("/:id/reply", ReplyController.queryReply.gate("获取文章评论列表"))
            Spark.post("/:id/reply", ReplyController.createReply.gate("参与文章评论"))
            Spark.post("/reply/:id/delete", ReplyController.delReply.gate("删除评论"))
        }

        Spark.path("/admin") {
            Spark.get("/article/list", AdminController.getArticleList.gate("管理员获取文章列表"))
            Spark.post("/user/:id/state", AdminController.userState.gate("修改用户状态"))
            Spark.post("/article/:id/state", AdminController.articleState.gate("修改文章状态"))
            Spark.post("/reply/:id/state", AdminController.replyState.gate("修改评论状态"))
        }
    }
}

fun Route.gate(desc: String, log: Boolean = true): Route {
    return Route { request, response ->
        val requestId = System.nanoTime()
        if (log) {
            val url = request.url()
            val method = request.requestMethod()
            val headers = request.headers().map { it to request.headers(it) }.toMap()
            val params = request.params()
            val queryParams = request.queryParams().map { it to request.queryParams(it) }.toMap()
            Log.d("Request", "$desc($requestId): ${Json.dumps(mapOf(
                    "url" to url,
                    "method" to method,
                    "headers" to headers,
                    "params" to params,
                    "query_params" to queryParams
            ))}")
        }
        var result: String
        try {
            result = this.handle(request, response) as String
            if (log) Log.d("Response", "$desc($requestId): $result")
        } catch (err: Abort) {
            result = Json.dumps(err.model)
            if (log) Log.d("Response", err)
            if (log) Log.d("Response", "$desc($requestId): $result")
        } catch (err: Throwable) {
            result = Json.dumps(mapOf("code" to Err.SYSTEM.code, "msg" to Err.SYSTEM.msg))
            Log.e("Response", err)
        }
        return@Route result
    }
}
