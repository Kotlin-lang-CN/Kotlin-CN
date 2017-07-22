package tech.kotlin.service.message

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CountGroupResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "订阅数")
    @JsonProperty("result")
    var result: List<Long> = ArrayList()

}