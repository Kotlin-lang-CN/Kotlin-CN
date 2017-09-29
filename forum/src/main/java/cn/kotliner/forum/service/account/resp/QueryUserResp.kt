package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.UserInfo
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryUserResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("account")
    var account: Map<Long, Account> = HashMap()

    @Protobuf(order = 2, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("info")
    var info: Map<Long, UserInfo> = HashMap()

}