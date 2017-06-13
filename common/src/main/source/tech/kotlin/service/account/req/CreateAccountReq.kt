package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Device

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class CreateAccountReq {

    @Protobuf(order = 1, required = true, fieldType = STRING, description = "用户名")
    @JsonProperty("username")
    var username: String = ""

    @Protobuf(order = 2, required = true, fieldType = STRING, description = "密码")
    @JsonProperty("password")
    var password: String = ""

    @Protobuf(order = 3, required = true, fieldType = STRING, description = "邮箱")
    @JsonProperty("email")
    var email: String = ""

    @Protobuf(order = 4, required = true, fieldType = OBJECT, description = "注册设备")
    @JsonProperty("device")
    var device: Device = Device()

}