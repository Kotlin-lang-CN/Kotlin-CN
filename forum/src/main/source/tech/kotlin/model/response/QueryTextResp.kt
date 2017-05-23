package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.TextContent

class QueryTextResp {
    @Protobuf(required = true, order = 1, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("result")
    var result: Map<Long, TextContent> = HashMap()
}