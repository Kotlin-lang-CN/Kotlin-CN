package cn.kotliner.forum.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ArticleResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章信息")
    @JsonProperty("article")
    var articleId: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "内容信息")
    @JsonProperty("content")
    var contentId: Long = 0

}