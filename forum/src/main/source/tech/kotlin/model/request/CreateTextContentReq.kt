package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateTextContentReq {

    @Protobuf(required = true, order = 1, fieldType = FieldType.STRING, description = "文本序号")
    @JsonProperty("serialize_id")
    var serializeId = ""

    @Protobuf(required = true, order = 2, fieldType = FieldType.STRING, description = "文本内容")
    @JsonProperty("content")
    var content = ""

}