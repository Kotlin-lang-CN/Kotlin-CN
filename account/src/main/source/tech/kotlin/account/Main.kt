package tech.kotlin.account

import spark.ResponseTransformer
import spark.Spark
import tech.kotlin.account.API.ACCOUNT
import tech.kotlin.account.controller.AccountController
import tech.kotlin.account.service.AccountServiceImpl
import tech.kotlin.account.service.TokenServiceImpl
import tech.kotlin.account.service.UserServiceImpl
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.serialize.Json
import tech.kotlin.mysql.Mysql
import tech.kotlin.redis.Redis
import tech.kotlin.service.Node
import tech.kotlin.service.account.AccountService
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/

object API {
    const val ACCOUNT = "account"
}

const val ROUTE = "/api/account"

val transformer = ResponseTransformer {
    if (it !is HashMap<*, *>) abort(Err.SYSTEM)
    @Suppress("UNCHECKED_CAST")
    it as HashMap<String, Any>
    it["code"] = 0
    it["msg"] = ""
    return@ResponseTransformer Json.dumps(it)
}

fun main(vararg args: String) {
    Redis.init(ACCOUNT)
    Mysql.init("init.sql")

    Node.init(ACCOUNT)
    Node[ACCOUNT].register(TokenService::class.java, TokenServiceImpl)
    Node[ACCOUNT].register(AccountService::class.java, AccountServiceImpl)
    Node[ACCOUNT].register(UserService::class.java, UserServiceImpl)

    Spark.port(9999)
    Spark.init()
    Spark.exception(Abort::class.java, { err, _, response -> response.body(Json.dumps(err.model)) })
    Spark.post("$ROUTE/login", AccountController.login, transformer)
    Spark.post("$ROUTE/register", AccountController.register, transformer)
    Spark.get("$ROUTE/:uid/info", AccountController.getUserInfo, transformer)
}

