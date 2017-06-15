package tech.kotlin.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Device

class GithubCheckTokenReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING)
    @JsonProperty("token")
    var token = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT)
    @JsonProperty("device")
    var device = Device()
}