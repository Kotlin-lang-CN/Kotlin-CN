package tech.kotlin.service.account

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.common.rpc.RpcInterface
import tech.kotlin.model.Account
import tech.kotlin.model.Device
import tech.kotlin.model.EmptyResp
import tech.kotlin.model.UserInfo
import tech.kotlin.service.Service.ACCOUNT_FREEZE
import tech.kotlin.service.Service.ACCOUNT_LOGIN
import tech.kotlin.service.Service.ACCOUNT_REGISTER

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface AccountService {

    @RpcInterface(ACCOUNT_REGISTER)
    fun create(req: CreateAccountReq): CreateAccountResp

    @RpcInterface(ACCOUNT_LOGIN)
    fun loginWithName(req: LoginReq): LoginResp

    @RpcInterface(ACCOUNT_FREEZE)
    fun freeze(req: FreezeAccountReq): EmptyResp

}

class CreateAccountReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "用户名")
    @JsonProperty("username")
    var username: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "密码")
    @JsonProperty("password")
    var password: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "邮箱")
    @JsonProperty("email")
    var email: String = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.OBJECT, description = "注册设备")
    @JsonProperty("device")
    var device: Device = Device()

}

class CreateAccountResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "账号")
    @JsonProperty("account")
    var account: Account = Account()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "JWT")
    @JsonProperty("token")
    var token: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("user_info")
    var userInfo: UserInfo = UserInfo()

}

class LoginReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "登录设备")
    @JsonProperty("device")
    var device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "用户名")
    @JsonProperty("login_name")
    var loginName: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "密码")
    @JsonProperty("password")
    var password: String = ""
}

class LoginResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "JWT")
    @JsonProperty("token")
    var token: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("user_info")
    var userInfo: UserInfo = UserInfo()

}

class FreezeAccountReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "operator uid")
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.MAP, description = "opereation")
    @JsonProperty("operation")
    var opeation: Map<Long, Int> = HashMap()

}


