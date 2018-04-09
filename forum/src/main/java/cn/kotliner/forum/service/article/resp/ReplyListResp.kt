package cn.kotliner.forum.service.article.resp

import cn.kotliner.forum.domain.model.Reply
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ReplyListResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "文章内容")
    @JsonProperty("articles")
    var result: List<Reply> = ArrayList()

}