package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.service.domain.Device

class CheckTokenReq() {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "JWT")
    @JsonProperty("token")
    var token: String = ""

    constructor(req: spark.Request) : this() {
        device = Device(req)
        token = req.headers("X-App-Token") ?: req.cookie("X-App-Token") ?: abort(Err.TOKEN_FAIL, "缺失登录信息")
    }
}