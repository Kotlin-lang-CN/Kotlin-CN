package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.GithubUser

class GithubBindReq {

    @Protobuf(order = 1, required = true, fieldType = OBJECT)
    @JsonProperty("github_user")
    var github = GithubUser()

}