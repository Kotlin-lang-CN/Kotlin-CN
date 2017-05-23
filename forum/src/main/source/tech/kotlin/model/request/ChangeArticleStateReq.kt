package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Article

class ChangeArticleStateReq {

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 1, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.UINT64, description = "文章id")
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    var id: Long = 0

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 2, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.UINT32, description = "修改封禁状态")
    @com.fasterxml.jackson.annotation.JsonProperty("state")
    var state: Int = tech.kotlin.model.domain.Article.State.NORMAL

}