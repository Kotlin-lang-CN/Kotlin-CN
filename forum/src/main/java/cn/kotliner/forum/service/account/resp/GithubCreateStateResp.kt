package cn.kotliner.forum.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubCreateStateResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "登录state")
    @JsonProperty("state")
    var state: String = ""

}