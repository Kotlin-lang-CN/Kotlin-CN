package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Article

class QueryArticleInOrderResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "查询文章")
    @JsonProperty("articles")
    var articles: List<Article> = ArrayList()

}