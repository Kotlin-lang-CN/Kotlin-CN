package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.UserInfo
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var account = Account()

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var user = UserInfo()

}