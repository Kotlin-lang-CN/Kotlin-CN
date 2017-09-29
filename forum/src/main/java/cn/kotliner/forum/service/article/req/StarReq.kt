package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class StarReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING)
    @JsonProperty("star_pool")
    var poolId: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64)
    @JsonProperty("owner")
    var owner: Long = 0L

}