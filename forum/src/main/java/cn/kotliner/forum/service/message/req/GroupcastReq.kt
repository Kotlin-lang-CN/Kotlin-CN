package cn.kotliner.forum.service.message.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GroupcastReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32, description = "解析类型")
    @JsonProperty("type")
    var type = 1

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "消息体")
    @JsonProperty("content")
    var content = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "消息创建者id")
    @JsonProperty("creator")
    var createor = 0L

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "消息接受者")
    @JsonProperty("group_id")
    var groupId = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT64, description = "排除用户id")
    @JsonProperty("exclude_uid")
    var excludeUID: List<Long> = ArrayList()

}