package tech.kotlin.service.message

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CountGroupReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "组id")
    @JsonProperty("ids")
    var ids: List<String> = ArrayList()

}