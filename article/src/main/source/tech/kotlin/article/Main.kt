package tech.kotlin.article

import spark.ResponseTransformer
import spark.Spark
import tech.kotlin.article.API.ARTICLE
import tech.kotlin.article.controller.ArticleController
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.serialize.Json
import tech.kotlin.mysql.Mysql
import tech.kotlin.redis.Redis
import tech.kotlin.service.Node

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object API {
    const val ACCOUNT = "account"
    const val ARTICLE = "article"
}

fun main(vararg args: String) {
    Redis.init("redis.json")
    Mysql.init("init.sql")

    Node.init(ARTICLE)

    Spark.port(9998)
    Spark.init()
    val ROUTE = "/api/article"
    val transformer = ResponseTransformer {
        if (it !is HashMap<*, *>) abort(Err.SYSTEM)
        @Suppress("UNCHECKED_CAST")
        it as HashMap<String, Any>
        it["code"] = 0
        it["msg"] = ""
        return@ResponseTransformer Json.dumps(it)
    }
    Spark.exception(Abort::class.java, { err, _, response -> response.body(Json.dumps(err.model)) })
    Spark.post("$ROUTE/post", ArticleController.postArticle, transformer)
    Spark.post("$ROUTE/post/:id/update", ArticleController.updateArticle, transformer)
    Spark.post("$ROUTE/post/:id", ArticleController.getArticleById, transformer)
}

