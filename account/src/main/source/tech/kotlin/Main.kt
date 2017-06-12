package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.PropRegistrator
import tech.kotlin.common.rpc.registrator.ServiceRegistrator
import tech.kotlin.controller.*
import tech.kotlin.utils.Mysql
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.int
import tech.kotlin.utils.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.dict
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.*
import tech.kotlin.common.utils.gate
import tech.kotlin.service.Accounts
import tech.kotlin.service.Sessions
import tech.kotlin.service.Users
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
    Accounts.initAdmin()//初始化管理员账号
    initRpcCgi()
    initHttpCgi()
}

fun initRpcCgi() {
    Serv.init(PropRegistrator(properties))
    Serv.register(AccountApi::class, Accounts)
    Serv.register(SessionApi::class, Sessions)
    Serv.register(UserApi::class, Users)
    Serv.publish(
            address = InetSocketAddress("0.0.0.0", properties int "deploy.service.${ServDef.ACCOUNT}.rpc"),
            serviceName = ServDef.ACCOUNT,
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    )
}

fun initHttpCgi() {
    port(properties int "deploy.service.${ServDef.ACCOUNT}.cgi")
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
        path("/admin") {
            get("/article/list", AdminController.getArticleList.gate("管理员获取文章列表"))
            post("/user/:id/state", AdminController.userState.gate("修改用户状态"))
            post("/article/:id/state", AdminController.articleState.gate("修改文章状态"))
            post("/reply/:id/state", AdminController.replyState.gate("修改评论状态"))
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