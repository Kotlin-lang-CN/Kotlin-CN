package tech.kotlin.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.GithubUser
import tech.kotlin.service.domain.UserInfo

class GithubAuthResp {

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 1, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.OBJECT, description = "github账号")
    @com.fasterxml.jackson.annotation.JsonProperty("github")
    var github = tech.kotlin.service.domain.GithubUser()

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 2, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.BOOL, description = "是否需要绑定账号")
    @com.fasterxml.jackson.annotation.JsonProperty("has_account")
    var hasAccount = false

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 3, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.OBJECT, description = "已绑定账号")
    @com.fasterxml.jackson.annotation.JsonProperty("account")
    var account = tech.kotlin.service.domain.Account()

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 4, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.OBJECT, description = "用户信息")
    @com.fasterxml.jackson.annotation.JsonProperty("user_info")
    var userInfo = tech.kotlin.service.domain.UserInfo()

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 5, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.STRING, description = "第三方账号登录token")
    @com.fasterxml.jackson.annotation.JsonProperty("session_token")
    var token = ""

}