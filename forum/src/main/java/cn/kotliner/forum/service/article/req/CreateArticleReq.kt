package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.*
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateArticleReq {

    @Protobuf(order = 1, required = true, fieldType = STRING, description = "标题")
    @JsonProperty("title")
    var title: String = ""

    @Protobuf(order = 2, required = true, fieldType = UINT64, description = "作者uid")
    @JsonProperty("author")
    var author: Long = 0

    @Protobuf(order = 3, required = true, fieldType = UINT32, description = "文章分类id")
    @JsonProperty("category")
    var category: Int = 0

    @Protobuf(order = 4, required = false, fieldType = STRING, description = "标签")
    @JsonProperty("tags")
    var tags: String = ""

    @Protobuf(order = 5, required = true, fieldType = STRING, description = "文章内容")
    @JsonProperty("content")
    var content: String = ""
}