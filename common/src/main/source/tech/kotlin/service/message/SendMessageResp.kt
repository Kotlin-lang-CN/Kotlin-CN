package tech.kotlin.service.message

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import tech.kotlin.service.domain.Message

class SendMessageResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP, description = "发送id")
    val msgMap: Map<Long, Message> = HashMap()

}