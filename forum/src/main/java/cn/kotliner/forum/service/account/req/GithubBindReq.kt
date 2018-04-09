package cn.kotliner.forum.service.account.req

import cn.kotliner.forum.domain.model.GithubUser
import com.baidu.bjf.remoting.protobuf.FieldType.OBJECT
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubBindReq {

    @Protobuf(order = 1, required = true, fieldType = OBJECT)
    @JsonProperty("github_user")
    var github = GithubUser()

}