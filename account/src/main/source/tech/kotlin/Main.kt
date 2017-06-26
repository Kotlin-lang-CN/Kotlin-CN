package tech.kotlin

import spark.Spark.*
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.rpc.registrator.PropRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.controller.AccountController
import tech.kotlin.controller.AdminController
import tech.kotlin.controller.GithubController
import tech.kotlin.service.*
import tech.kotlin.service.account.*
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
    Accounts.initAdmin()//初始化管理员账号
    initRpcCgi(if (args.isNotEmpty()) args[0] else "")
    initHttpCgi(if (args.size >= 2) args[1] else "")
}

fun initRpcCgi(publishPort: String) {
    Serv.init(EtcdRegistrator(properties))
    Serv.register(AccountApi::class, Accounts)
    Serv.register(EmailApi::class, Emails)
    Serv.register(GithubApi::class, Githubs)
    Serv.register(SessionApi::class, Sessions)
    Serv.register(UserApi::class, Users)
    val port = publishPort.tryExec(Err.SYSTEM, "illegal publish host $publishPort") { it.toInt() }
    Serv.publish(
            broadcastIp = properties str "deploy.broadcast.host", port = port,
            serviceName = ServDef.ACCOUNT, executorService = Executors.newFixedThreadPool(20)
    )
}

fun initHttpCgi(cgiPort: String) {
    if (cgiPort.isNullOrBlank()) return
    Log.i("init cgi port @ $cgiPort")
    port(cgiPort.toInt())
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
            get("/state", GithubController.createState.gate("获取登录会话"))
            post("/auth", GithubController.auth.gate("github登录"))

        }
        path("/admin") {
            get("/article/list", AdminController.getArticleList.gate("管理员获取文章列表"))
            post("/user/:id/state", AdminController.userState.gate("修改用户状态"))
            post("/article/:id/state", AdminController.articleState.gate("修改文章状态"))
            post("/reply/:id/state", AdminController.replyState.gate("修改评论状态"))
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