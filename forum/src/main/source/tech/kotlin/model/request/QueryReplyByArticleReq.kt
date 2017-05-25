package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryReplyByArticleReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("article_id")
    var articleId: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "查询偏移值")
    @JsonProperty("offset")
    var offset: Int = 0

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT32, description = "查询长度")
    @JsonProperty("limit")
    var limit: Int = 20

}