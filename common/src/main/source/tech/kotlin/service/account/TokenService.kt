package tech.kotlin.service.account

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import spark.Request
import tech.kotlin.common.rpc.RpcInterface
import tech.kotlin.model.Account
import tech.kotlin.model.Device
import tech.kotlin.service.Service.ACCOUNT_CHECK_TOKEN
import tech.kotlin.service.Service.ACCOUNT_CREATE_SESSION

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface TokenService {

    @RpcInterface(ACCOUNT_CHECK_TOKEN)
    fun checkToken(req: CheckTokenReq): CheckTokenResp

    @RpcInterface(ACCOUNT_CREATE_SESSION)
    fun createSession(req: CreateSessionReq): CreateSessionResp
}

class CheckTokenReq() {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "JWT")
    @JsonProperty("token")
    var token: String = ""

    constructor(req: Request) : this() {
        device = Device(req)
        token = req.headers("X-App-Token")
        assert(!token.isNullOrBlank())
    }
}

class CheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var account: Account = Account()

}

class CreateSessionReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "创建会话")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "账号")
    var uid: Long = 0

}

class CreateSessionResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "会话token")
    @JsonProperty("token")
    var token: String = ""

}

class SessionContent {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "会话id")
    @JsonProperty("id")
    var id: Long = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "用户id")
    @JsonProperty("uid")
    var uid: Long = 0

    fun isEqual(session: SessionContent): Boolean {
        return this.device.isEquals(session.device) && this.uid == session.uid
    }
}

