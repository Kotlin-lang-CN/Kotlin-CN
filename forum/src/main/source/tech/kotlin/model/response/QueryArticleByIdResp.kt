package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Article

class QueryArticleByIdResp {

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 1, required = false, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.MAP, description = "文章查询结果")
    @com.fasterxml.jackson.annotation.JsonProperty("articles")
    var articles: Map<Long, tech.kotlin.model.domain.Article> = HashMap()

}