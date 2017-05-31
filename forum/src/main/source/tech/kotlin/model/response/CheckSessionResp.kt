package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo

class CheckSessionResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var account = Account()

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var user = UserInfo()

}