package tech.kotlin.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryTextReq {

    @Protobuf(required = true, order = 1, fieldType = FieldType.UINT64, description = "id")
    @JsonProperty("id")
    var id: List<Long> = ArrayList()

}