package cn.kotliner.forum.service.article.resp

import cn.kotliner.forum.domain.Article
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ArticleListResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "查询结果")
    @JsonProperty("result")
    var result: List<Article> = ArrayList()

}