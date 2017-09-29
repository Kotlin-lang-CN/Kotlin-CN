package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.UINT32
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryReplyByArticleReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "文章id")
    @JsonProperty("article_id")
    var articleId: Long = 0

    @Protobuf(order = 2, required = true, fieldType = UINT32, description = "查询偏移值")
    @JsonProperty("offset")
    var offset: Int = 0

    @Protobuf(order = 3, required = true, fieldType = UINT32, description = "查询长度")
    @JsonProperty("limit")
    var limit: Int = 20

}