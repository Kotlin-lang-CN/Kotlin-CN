package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Reply

class ReplyListResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "文章内容")
    @JsonProperty("articles")
    var result: List<Reply> = ArrayList()

}