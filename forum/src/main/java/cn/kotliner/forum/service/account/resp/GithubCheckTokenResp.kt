package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.model.GithubUser
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubCheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT)
    @JsonProperty("github_user_info")
    var info = GithubUser()
}