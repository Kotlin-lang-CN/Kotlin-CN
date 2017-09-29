package cn.kotliner.forum.service.message.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryMessageReq {

    @Protobuf(order = 1, fieldType = FieldType.UINT64, required = true)
    @JsonProperty("uid")
    var uid = 0L

    @Protobuf(order = 2, fieldType = FieldType.UINT32, required = true)
    @JsonProperty("offset")
    var offset = 0

    @Protobuf(order = 3, fieldType = FieldType.UINT32, required = true)
    @JsonProperty("limit")
    var limit = 20

}