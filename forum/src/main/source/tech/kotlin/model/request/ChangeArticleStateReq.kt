package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Article

class ChangeArticleStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "修改封禁状态")
    @JsonProperty("state")
    var state: Int = Article.State.NORMAL

}