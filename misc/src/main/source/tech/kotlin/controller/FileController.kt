package tech.kotlin.controller

import com.qiniu.util.Auth
import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.ok
import tech.kotlin.common.utils.str
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object FileController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)
    val accessKey = Props str "file.qiniu.access.key"
    val secretKey = Props str "file.qiniu.access.secret"
    val bucket = Props str "file.qiniu.bucket"

    val qiniuToken = Route { req, _ ->
        sessionApi.checkToken(CheckTokenReq(req))
        val auth = Auth.create(accessKey, secretKey)
        val token = auth.uploadToken(bucket)
        return@Route  ok { it["token"] = token }
    }
}