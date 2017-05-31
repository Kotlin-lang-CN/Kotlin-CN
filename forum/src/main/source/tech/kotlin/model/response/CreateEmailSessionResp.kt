package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateEmailSessionResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "token")
    @JsonProperty("token")
    var token: String = ""

}