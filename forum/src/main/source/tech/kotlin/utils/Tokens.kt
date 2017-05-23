package tech.kotlin.utils

import spark.Request
import tech.kotlin.utils.exceptions.Abort
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.tryExec
import tech.kotlin.model.domain.Device
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.service.account.TokenService

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Tokens {

    @Throws(Abort::class)
    fun checkToken(service: TokenService, req: Request) {
        TokenService.checkToken(CheckTokenReq().apply {
            this.token = req.headers("X-App-Token")
            this.device = tryExec(Err.PARAMETER, "无效的设备信息") { Device(req) }
        })
    }

}