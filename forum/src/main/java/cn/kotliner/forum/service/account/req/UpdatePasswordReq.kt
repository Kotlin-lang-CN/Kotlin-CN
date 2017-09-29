package cn.kotliner.forum.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class UpdatePasswordReq {
    @Protobuf(required = true, order = 1, fieldType = UINT64, description = "用户id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(required = true, order = 2, fieldType = STRING, description = "密码内容")
    @JsonProperty("password")
    var password: String = ""
}