package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.model.Device
import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf

class CreateSessionReq {

    @Protobuf(order = 1, required = true, fieldType = OBJECT, description = "创建会话")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = UINT64, description = "账号")
    var uid: Long = 0

}