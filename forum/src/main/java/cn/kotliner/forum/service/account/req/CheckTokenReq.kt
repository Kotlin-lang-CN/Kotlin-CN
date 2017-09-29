package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.Device
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CheckTokenReq() {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "JWT")
    @JsonProperty("token")
    var token: String = ""

}