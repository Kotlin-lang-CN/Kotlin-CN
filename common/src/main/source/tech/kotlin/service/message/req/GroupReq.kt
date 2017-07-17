package tech.kotlin.service.message.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GroupReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "用户id")
    @JsonProperty("uid")
    var uid: List<Long> = ArrayList()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "组id")
    @JsonProperty("group_id")
    var groupId = ""

}