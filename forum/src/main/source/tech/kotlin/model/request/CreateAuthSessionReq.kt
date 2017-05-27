package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Device

class CreateAuthSessionReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "登录设备")
    @JsonProperty("device")
    var device: Device = Device()
}