package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.model.Device
import cn.kotliner.forum.domain.model.GithubUser
import com.baidu.bjf.remoting.protobuf.FieldType.*
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

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

    @Protobuf(order = 5, required = false, fieldType = OBJECT)
    @JsonProperty("github")
    var githubUser = GithubUser()

}