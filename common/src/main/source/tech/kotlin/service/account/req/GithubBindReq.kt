package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.FieldType.*
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Device

class GithubBindReq {
    @Protobuf(order = 1, required = true, fieldType = STRING)
    @JsonProperty("token")
    var token = ""

    @Protobuf(order = 2, required = true, fieldType = UINT64)
    @JsonProperty("github_id")
    var githubId = 0L

    @Protobuf(order = 3, required = true, fieldType = UINT64)
    @JsonProperty("bind_uid")
    var bindUID = 0L

    @Protobuf(order = 4, required = true, fieldType = OBJECT)
    @JsonProperty("device")
    var device = Device()
}