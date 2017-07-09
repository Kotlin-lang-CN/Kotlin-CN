package tech.kotlin.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CountArticleByAuthorReq {
    @Protobuf(order = 1, fieldType = FieldType.UINT64, required = true)
    @JsonProperty("author")
    var author: List<Long> = ArrayList()
}