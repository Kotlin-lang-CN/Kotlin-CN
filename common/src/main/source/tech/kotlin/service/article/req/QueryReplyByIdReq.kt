package tech.kotlin.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryReplyByIdReq {
    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "查询id")
    @JsonProperty("id")
    var id: List<Long> = ArrayList()
}