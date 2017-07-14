package tech.kotlin.service.message.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class MarkReadReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "消息id")
    @JsonProperty("id")
    var id = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "消息接受者")
    @JsonProperty("acceptor")
    var acceptor = 0L

}