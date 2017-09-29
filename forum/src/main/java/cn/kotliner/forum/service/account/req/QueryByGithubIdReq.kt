package cn.kotliner.forum.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryByGithubIdReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64)
    @JsonProperty("id")
    var githubId: List<Long> = ArrayList()

}