package tech.kotlin.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateSessionResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "会话token")
    @JsonProperty("token")
    var token: String = ""

}