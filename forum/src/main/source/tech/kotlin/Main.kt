package tech.kotlin

import spark.Route
import spark.Spark
import tech.kotlin.controller.AccountController
import tech.kotlin.controller.ArticleController
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
    Redis.init(config = "redis.json")
    Mysql.init(config = "mybatis.xml", properties = properties, sql = "init.sql")

    Spark.port(properties int "deploy.port")
    Spark.init()
    Spark.post("/account/login", AccountController.login.gate())
    Spark.post("/account/register", AccountController.register.gate())
    Spark.get("/account/user/:uid", AccountController.getUserInfo.gate())
    Spark.post("/account/user/:uid/password", AccountController.alterPassword.gate(true))
    Spark.post("/account/user/:uid/update", AccountController.updateUserInfo.gate())

    Spark.post("/article/post", ArticleController.postArticle.gate())
    Spark.post("/article/post/:id/update", ArticleController.updateArticle.gate())
    Spark.get("/article/post/:id", ArticleController.getArticleById.gate())
}

fun Route.gate(log: Boolean = false): Route {
    return Route { request, response ->
        if (log) {
            val url = request.url()
            val method = request.requestMethod()
            val headers = request.headers().map { it to request.headers(it) }.toMap()
            val params = request.params()
            val queryParams = request.queryParams().map { it to request.queryParams(it) }.toMap()
            Log.d("Request", Json.dumps(mapOf(
                    "url" to url,
                    "method" to method,
                    "headers" to headers,
                    "params" to params,
                    "query_params" to queryParams
            )))
        }
        var result: String
        try {
            result = this.handle(request, response) as String
            if (log) Log.d("Response", result)
        } catch (err: Abort) {
            result = Json.dumps(err.model)
            if (log) Log.d("Response", result)
        } catch (err: Throwable) {
            result = Json.dumps(mapOf("code" to Err.SYSTEM.code, "msg" to Err.SYSTEM.msg))
            Log.e("Response", err)
        }
        return@Route result
    }
}
