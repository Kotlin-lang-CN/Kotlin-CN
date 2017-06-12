package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateArticleReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "标题")
    @JsonProperty("title")
    var title: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "作者uid")
    @JsonProperty("author")
    var author: Long = 0

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT32, description = "文章分类id")
    @JsonProperty("category")
    var category: Int = 0

    @Protobuf(order = 4, required = false, fieldType = FieldType.STRING, description = "标签")
    @JsonProperty("tags")
    var tags: String = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.STRING, description = "文章内容")
    @JsonProperty("content")
    var content: String = ""
}