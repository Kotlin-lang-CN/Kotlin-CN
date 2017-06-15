package tech.kotlin.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.GithubUser

class GithubCheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT)
    @JsonProperty("github_user_info")
    var info = GithubUser()
}