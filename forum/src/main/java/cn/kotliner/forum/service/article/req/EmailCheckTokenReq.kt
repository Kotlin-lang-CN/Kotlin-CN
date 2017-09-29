package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class EmailCheckTokenReq {

    @Protobuf(order = 1, required = true, fieldType = STRING)
    @JsonProperty("token")
    var token = ""

}