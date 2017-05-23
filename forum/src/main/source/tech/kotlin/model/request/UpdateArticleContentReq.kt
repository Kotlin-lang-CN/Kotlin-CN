package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class UpdateArticleContentReq {

    @Protobuf(required = true, order = 1, fieldType = FieldType.UINT64, description = "修改人uid")
    @JsonProperty("editor_uid")
    var editorUid: Long = 1

    @Protobuf(required = true, order = 2, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(required = true, order = 3, fieldType = FieldType.STRING, description = "文章内容")
    @JsonProperty("content")
    var content: String = ""
}