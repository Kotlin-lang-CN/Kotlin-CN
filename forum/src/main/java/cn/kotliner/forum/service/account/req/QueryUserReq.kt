package cn.kotliner.forum.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryUserReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "查询用户id")
    @JsonProperty("id")
    var id: List<Long> = ArrayList()

}