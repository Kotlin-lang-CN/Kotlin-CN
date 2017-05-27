package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Device

class GithubAuthReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "github授权code")
    @JsonProperty("code")
    var code: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "授权会话state")
    @JsonProperty("state")
    var state: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

}