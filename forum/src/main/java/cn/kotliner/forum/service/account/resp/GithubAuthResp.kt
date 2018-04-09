package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.GithubUser
import cn.kotliner.forum.domain.model.UserInfo
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class GithubAuthResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "github账号")
    @JsonProperty("github")
    var github = GithubUser()

    @Protobuf(order = 2, required = true, fieldType = FieldType.BOOL, description = "是否需要绑定账号")
    @JsonProperty("has_account")
    var hasAccount = false

    @Protobuf(order = 3, required = true, fieldType = FieldType.OBJECT, description = "已绑定账号")
    @JsonProperty("account")
    var account = Account()

    @Protobuf(order = 4, required = true, fieldType = FieldType.OBJECT, description = "用户信息")
    @JsonProperty("user_info")
    var userInfo = UserInfo()

    @Protobuf(order = 5, required = true, fieldType = FieldType.STRING, description = "第三方账号登录token")
    @JsonProperty("session_token")
    var token = ""

}