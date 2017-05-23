package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
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