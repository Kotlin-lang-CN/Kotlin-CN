package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.Device
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubCreateStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "登录设备")
    @JsonProperty("device")
    var device: Device = Device()
}