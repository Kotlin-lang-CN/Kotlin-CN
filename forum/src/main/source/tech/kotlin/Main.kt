package tech.kotlin

import spark.Route
import spark.Spark
import tech.kotlin.controller.AccountController
import tech.kotlin.controller.ArticleController
import tech.kotlin.controller.ArticleViewController
import tech.kotlin.controller.ReplyController
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

    Spark.post("/api/account/login", AccountController.login.gate())
    Spark.post("/api/account/register", AccountController.register.gate())
    Spark.get("/api/account/user/:uid", AccountController.getUserInfo.gate())
    Spark.post("/api/account/user/:uid/password", AccountController.alterPassword.gate())
    Spark.post("/api/account/user/:uid/update", AccountController.updateUserInfo.gate())

    Spark.post("/api/article/post", ArticleController.postArticle.gate())
    Spark.get("/api/article/post/:id", ArticleController.getArticleById.gate())
    Spark.post("/api/article/post/:id/update", ArticleController.updateArticle.gate())

    Spark.get("/api/article/list", ArticleViewController.getList.gate())
    Spark.get("/api/article/fine", ArticleViewController.getFine.gate())
    Spark.get("/api/article/category/:category_id", ArticleViewController.getByCategory.gate())
    Spark.get("/api/article/mine", ArticleViewController.getMine.gate())

    Spark.get("/api/article/:id/reply", ReplyController.queryReply.gate())
    Spark.post("/api/article/:id/reply", ReplyController.createReply.gate())
    Spark.post("/api/article/reply/:reply_id/delete", ReplyController.delReply.gate())
    Spark.post("/api/article/reply/:reply_id/change_state", ReplyController.control.gate())
}

fun Route.gate(log: Boolean = true): Route {
    return Route { request, response ->
        val requestId = System.nanoTime()
        if (log) {
            val url = request.url()
            val method = request.requestMethod()
            val headers = request.headers().map { it to request.headers(it) }.toMap()
            val params = request.params()
            val queryParams = request.queryParams().map { it to request.queryParams(it) }.toMap()
            Log.d("Request", "$requestId: ${Json.dumps(mapOf(
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
            if (log) Log.d("Response", "$requestId: $result")
        } catch (err: Abort) {
            result = Json.dumps(err.model)
            if (log) Log.d("Response", "$requestId: $result")
        } catch (err: Throwable) {
            result = Json.dumps(mapOf("code" to Err.SYSTEM.code, "msg" to Err.SYSTEM.msg))
            Log.e("Response", err)
        }
        return@Route result
    }
}
