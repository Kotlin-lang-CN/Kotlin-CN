package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.model.Device
import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubAuthReq {

    @Protobuf(order = 1, required = true, fieldType = STRING, description = "github授权code")
    @JsonProperty("code")
    var code: String = ""

    @Protobuf(order = 2, required = true, fieldType = STRING, description = "授权会话state")
    @JsonProperty("state")
    var state: String = ""

    @Protobuf(order = 3, required = true, fieldType = OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

}