package cn.kotliner.forum.service.message.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryGroupStateResp {

    @Protobuf(order = 1, fieldType = FieldType.MAP, required = true)
    @JsonProperty("result")
    var result: Map<Long, Boolean> = HashMap()

}