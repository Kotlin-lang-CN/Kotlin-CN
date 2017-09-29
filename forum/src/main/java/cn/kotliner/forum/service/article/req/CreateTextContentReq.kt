package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateTextContentReq {

    @Protobuf(required = true, order = 1, fieldType = STRING, description = "文本序号")
    @JsonProperty("serialize_id")
    var serializeId = ""

    @Protobuf(required = true, order = 2, fieldType = STRING, description = "文本内容")
    @JsonProperty("content")
    var content = ""

}