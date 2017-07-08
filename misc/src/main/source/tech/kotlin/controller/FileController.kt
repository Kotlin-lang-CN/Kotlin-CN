package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object FileController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)

    val qiniuToken = Route { req, _ ->


    }
}