package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateArticleReq {

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 1, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.STRING, description = "标题")
    @com.fasterxml.jackson.annotation.JsonProperty("title")
    var title: String = ""

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 2, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.UINT64, description = "作者uid")
    @com.fasterxml.jackson.annotation.JsonProperty("author")
    var author: Long = 0

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 3, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.UINT32, description = "文章分类id")
    @com.fasterxml.jackson.annotation.JsonProperty("category")
    var category: Int = 0

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 4, required = false, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.STRING, description = "标签")
    @com.fasterxml.jackson.annotation.JsonProperty("tags")
    var tags: String = ""

}