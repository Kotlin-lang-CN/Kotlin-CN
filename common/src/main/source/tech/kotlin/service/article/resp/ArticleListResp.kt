package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Article

class ArticleListResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "查询结果")
    @JsonProperty("result")
    var result: List<Article> = ArrayList()

}