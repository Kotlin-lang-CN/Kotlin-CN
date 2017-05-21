package tech.kotlin.common.utils

import spark.Request
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.tryExec
import tech.kotlin.model.Device
import tech.kotlin.service.account.CheckTokenReq
import tech.kotlin.service.account.TokenService

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Tokens {

    @Throws(Abort::class)
    fun checkToken(service: TokenService, req: Request) {
        service.checkToken(CheckTokenReq().apply {
            this.token = req.headers("X-App-Token")
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
        })
    }

}