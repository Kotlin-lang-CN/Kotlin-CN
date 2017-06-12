package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Reply

class ChangeReplyStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "评论id")
    @JsonProperty("reply_id")
    var replyId = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "状态")
    @JsonProperty("state")
    var state: Int = Reply.State.NORMAL

}