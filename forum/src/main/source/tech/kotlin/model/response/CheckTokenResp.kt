package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Account

class CheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("account")
    var account: Account = Account()

}