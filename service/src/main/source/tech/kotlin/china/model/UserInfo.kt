package tech.kotlin.china.model

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class UserInfo {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "用户uid")
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "用户名")
    @JsonProperty("username")
    var name: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "用户名")
    @JsonProperty("logo")
    var logo: String = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "邮箱")
    @JsonProperty("email")
    var email: String = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.BOOL, description = "邮箱验证标志")
    @JsonProperty("is_email_verified")
    var isEmailVerified: Boolean = false


}