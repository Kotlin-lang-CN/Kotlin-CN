package tech.kotlin.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.UserInfo

class CheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var account = Account()

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var user = UserInfo()

}