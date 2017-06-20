package tech.kotlin.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class FlowerReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING)
    @JsonProperty("star_pool")
    var starPool: List<String> = ArrayList()

}