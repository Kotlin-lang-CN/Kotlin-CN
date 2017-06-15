package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.GithubUser

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class LoginReq {

    @Protobuf(order = 1, required = true, fieldType = OBJECT, description = "登录设备")
    @JsonProperty("device")
    var device = tech.kotlin.service.domain.Device()

    @Protobuf(order = 2, required = true, fieldType = STRING, description = "用户名")
    @JsonProperty("login_name")
    var loginName: String = ""

    @Protobuf(order = 3, required = true, fieldType = STRING, description = "密码")
    @JsonProperty("password")
    var password: String = ""

    @Protobuf(order = 4, required = false, fieldType = OBJECT)
    @JsonProperty("github")
    var githubUser = GithubUser()
}