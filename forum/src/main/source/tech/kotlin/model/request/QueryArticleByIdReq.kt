package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryArticleByIdReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "查询id")
    @JsonProperty("ids")
    var ids = ArrayList<Long>()

}