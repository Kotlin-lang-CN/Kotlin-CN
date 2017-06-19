package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CountStarResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP)
    @JsonProperty("result")
    var result: Map<String, Int> = HashMap()

}