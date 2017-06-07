package tech.kotlin

import spark.Spark.*
import tech.kotlin.controller.*
import tech.kotlin.utils.Mysql
import tech.kotlin.common.os.LooperApp
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.int
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.dict

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val properties = Props.loads("project.properties")

fun main(vararg args: String) = LooperApp.start({

    Redis.init(properties)
    Mysql.init(config = "mybatis.xml", properties = properties, sql = "init.sql")

    port(properties int "deploy.port")
    init()
    path("/api") {
        path("/account") {
            post("/login", AccountController.login.gate("用户登录"))
            post("/register", AccountController.register.gate("用户注册"))
            post("/user/:uid/password", AccountController.alterPassword.gate("修改密码"))
            post("/user/:uid/update", AccountController.updateUserInfo.gate("更新用户信息"))
            get("/user/:uid", AccountController.getUserInfo.gate("查询用户信息"))
            get("/email/activate", AccountController.activateEmail.gate("激活账户邮箱"))
        }

        path("/github") {
            get("/oauth", GithubController.startAuth.gate("获取登录会话"))
            get("/callback", GithubController.authCallback.gate("github第三方回调"))
        }

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

        path("/admin") {
            get("/article/list", AdminController.getArticleList.gate("管理员获取文章列表"))
            post("/user/:id/state", AdminController.userState.gate("修改用户状态"))
            post("/article/:id/state", AdminController.articleState.gate("修改文章状态"))
            post("/reply/:id/state", AdminController.replyState.gate("修改评论状态"))
        }

        path("/rss") {
            get("/fine", ArticleViewController.rssFine)
        }

        path("/misc") {
            get("/dashboard", MiscController.dashboard.gate(""))
        }
    }
    notFound { req, response ->
        response.header("Access-Control-Allow-Origin", "http://localhost:3000")
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
}, args)
