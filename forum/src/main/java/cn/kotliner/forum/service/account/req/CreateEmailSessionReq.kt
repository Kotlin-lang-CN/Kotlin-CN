package cn.kotliner.forum.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateEmailSessionReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "验证请求")
    @JsonProperty("uid")
    var uid: Long = 0L

    @Protobuf(order = 2, required = true, fieldType = STRING, description = "邮件地址")
    @JsonProperty("email")
    var email: String = ""

}