package tech.kotlin.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class UpdateArticleContentReq {

    @Protobuf(required = true, order = 1, fieldType = UINT64, description = "修改人uid")
    @JsonProperty("editor_uid")
    var editorUid: Long = 1

    @Protobuf(required = true, order = 2, fieldType = UINT64, description = "文章id")
    @JsonProperty("article_id")
    var articleId: Long = 0

    @Protobuf(required = true, order = 3, fieldType = STRING, description = "文章内容")
    @JsonProperty("content")
    var content: String = ""
}