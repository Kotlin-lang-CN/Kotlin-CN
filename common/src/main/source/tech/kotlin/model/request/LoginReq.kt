package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Device

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
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