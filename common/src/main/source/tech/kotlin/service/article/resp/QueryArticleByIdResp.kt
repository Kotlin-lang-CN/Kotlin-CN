package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Article

class QueryArticleByIdResp {

    @Protobuf(order = 1, required = false, fieldType = FieldType.MAP, description = "文章查询结果")
    @JsonProperty("articles")
    var articles: Map<Long, Article> = HashMap()

}