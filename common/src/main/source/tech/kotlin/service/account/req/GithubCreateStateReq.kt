package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Device

class GithubCreateStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "登录设备")
    @JsonProperty("device")
    var device: Device = Device()
}