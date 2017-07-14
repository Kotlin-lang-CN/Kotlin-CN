package tech.kotlin.service.message.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.UserInfo

class ListGroupResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "组内用户信息")
    @JsonProperty("user")
    var user: List<UserInfo> = ArrayList()

}