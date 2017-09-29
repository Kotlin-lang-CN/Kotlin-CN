package cn.kotliner.forum.service.message.resp

import cn.kotliner.forum.domain.Message
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryMessageResp {

    @Protobuf(order = 1, fieldType = FieldType.OBJECT, required = true)
    @JsonProperty("msgs")
    var msgs: List<Message> = ArrayList()

}