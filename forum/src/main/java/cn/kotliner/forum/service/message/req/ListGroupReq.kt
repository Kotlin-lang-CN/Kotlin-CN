package cn.kotliner.forum.service.message.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ListGroupReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "组id")
    @JsonProperty("group_id")
    var groupId = ""

    @Protobuf(order = 2, fieldType = FieldType.UINT32, required = true)
    @JsonProperty("offset")
    var offset = 0

    @Protobuf(order = 3, fieldType = FieldType.UINT32, required = true)
    @JsonProperty("limit")
    var limit = 20

}