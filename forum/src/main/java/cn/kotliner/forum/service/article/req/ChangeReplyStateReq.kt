package cn.kotliner.forum.service.article.req

import cn.kotliner.forum.domain.Reply
import com.baidu.bjf.remoting.protobuf.FieldType.UINT32
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ChangeReplyStateReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "评论id")
    @JsonProperty("reply_id")
    var replyId = 0L

    @Protobuf(order = 2, required = true, fieldType = UINT32, description = "状态")
    @JsonProperty("state")
    var state: Int = Reply.State.NORMAL

}