package cn.kotliner.forum.service.message.resp

import cn.kotliner.forum.domain.model.UserInfo
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ListGroupResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "组内用户信息")
    @JsonProperty("user")
    var user: List<UserInfo> = ArrayList()

}