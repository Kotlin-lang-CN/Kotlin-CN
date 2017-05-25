package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Article

class QueryLatestArticleReq {
    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32, description = "查询偏移值")
    @JsonProperty("offset")
    var offset = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "数量限制")
    @JsonProperty("limit")
    var limit = 0

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "文章类型")
    @JsonProperty("category")
    var category = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "文章状态")
    @JsonProperty("state")
    var state = "${Article.State.FINE},${Article.State.NORMAL}"
}