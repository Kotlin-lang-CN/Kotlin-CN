package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateAuthSessionResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "登录state")
    @JsonProperty("state")
    var state: String = ""

}