package tech.kotlin

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import spark.Spark
import spark.Spark.*
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.dict
import tech.kotlin.common.utils.gate
import tech.kotlin.common.utils.str
import tech.kotlin.controller.AccountController
import tech.kotlin.controller.AdminController
import tech.kotlin.controller.GithubController
import tech.kotlin.controller.ProfileController
import tech.kotlin.service.*
import tech.kotlin.service.account.*
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

    AccountService.initAdmin()//初始化管理员账号
}

fun initService() {
    Serv.init(EtcdRegistrator(Props))

    Serv.register(AccountApi::class, AccountService)
    Serv.register(EmailApi::class, EmailService)
    Serv.register(GithubApi::class, GithubService)
    Serv.register(SessionApi::class, SessionService)
    Serv.register(UserApi::class, UserService)
    Serv.register(ProfileApi::class, ProfileService)

    Serv.publish(broadcastIp = Props str "deploy.broadcast.host",
                 port = Launcher.publish,
                 serviceName = ServDef.ACCOUNT,
                 executorService = Executors.newFixedThreadPool(20))
}

fun initHttpServer() {
    Spark.port(Launcher.http)
    Spark.init()
    path("/api") {
        path("/account") {
            //登录注册会话
            post("/login", AccountController.login.gate("用户登录"))
            post("/register", AccountController.register.gate("用户注册"))

            //用户信息
            post("/user/:uid/password", AccountController.alterPassword.gate("修改密码"))
            post("/user/:uid/update", AccountController.updateUserInfo.gate("更新用户信息"))
            get("/user/:uid", AccountController.getUserInfo.gate("查询用户信息"))
            get("/email/activate", AccountController.activateEmail.gate("激活账户邮箱"))

            //用户资料
            post("/profile/update", ProfileController.updateProfile.gate("更新用户资料"))
            get("/profile", ProfileController.getProfile.gate("获取用户资料"))
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