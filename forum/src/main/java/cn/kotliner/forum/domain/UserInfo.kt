package cn.kotliner.forum.domain

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
    var username: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "用户名")
    @JsonProperty("logo")
    var logo: String = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "邮箱")
    @JsonProperty("email")
    var email: String = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT32, description = "邮箱验证标志")
    @JsonProperty("email_state")
    var emailState: Int = EmailState.TO_BE_VERIFY

    object EmailState {
        const val TO_BE_VERIFY = 0
        const val VERIFIED = 1
    }

}