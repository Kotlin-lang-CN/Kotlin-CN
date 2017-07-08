package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CountArticleByAuthorResp {
    @Protobuf(order = 1, fieldType = FieldType.MAP, required = true)
    @JsonProperty("result")
    var result: Map<Long, Int> = HashMap()
}