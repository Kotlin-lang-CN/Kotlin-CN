package tech.kotlin.service.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Device

class AccountSession {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "会话id")
    @JsonProperty("id")
    var id: Long = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "用户id")
    @JsonProperty("uid")
    var uid: Long = 0

    fun isEqual(session: AccountSession): Boolean {
        return this.device.isEquals(session.device) && this.uid == session.uid
    }

}