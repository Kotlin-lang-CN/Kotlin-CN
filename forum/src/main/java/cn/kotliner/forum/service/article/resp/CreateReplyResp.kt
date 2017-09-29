package cn.kotliner.forum.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateReplyResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "评论id")
    @JsonProperty("reply_id")
    var replyId: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "评论内容id")
    @JsonProperty("content_id")
    var contentId: Long = 0

}