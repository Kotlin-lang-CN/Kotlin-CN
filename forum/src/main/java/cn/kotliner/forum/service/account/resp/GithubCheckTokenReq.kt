package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.model.Device
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubCheckTokenReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING)
    @JsonProperty("token")
    var token = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT)
    @JsonProperty("device")
    var device = Device()
}