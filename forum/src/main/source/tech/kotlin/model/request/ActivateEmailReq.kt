package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ActivateEmailReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING)
    @JsonProperty("token")
    var token = ""

}