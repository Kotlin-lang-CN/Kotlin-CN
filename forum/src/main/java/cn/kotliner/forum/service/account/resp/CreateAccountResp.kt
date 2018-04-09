package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.UserInfo
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

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