package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class EmailReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "接收者")
    @JsonProperty("to")
    var to = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "标题")
    @JsonProperty("subject")
    var subject = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "内容")
    @JsonProperty("content")
    var content = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.BOOL, description = "异步发送")
    @JsonProperty("async_flag")
    var async = true
}