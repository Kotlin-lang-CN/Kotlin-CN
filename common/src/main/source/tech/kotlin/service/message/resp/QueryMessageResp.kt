package tech.kotlin.service.message.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Message

class QueryMessageResp {

    @Protobuf(order = 1, fieldType = FieldType.OBJECT, required = true)
    @JsonProperty("msgs")
    var msgs: List<Message> = ArrayList()

}