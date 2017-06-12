package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateEmailSessionReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "验证请求")
    @JsonProperty("uid")
    var uid: Long = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "邮件地址")
    @JsonProperty("email")
    var email: String = ""

}