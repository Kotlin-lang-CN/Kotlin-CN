package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.BOOL
import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class EmailReq {

    @Protobuf(order = 1, required = true, fieldType = STRING, description = "接收者")
    @JsonProperty("to")
    var to = ""

    @Protobuf(order = 2, required = true, fieldType = STRING, description = "标题")
    @JsonProperty("subject")
    var subject = ""

    @Protobuf(order = 3, required = true, fieldType = STRING, description = "内容")
    @JsonProperty("content")
    var content = ""

    @Protobuf(order = 4, required = true, fieldType = BOOL, description = "异步发送")
    @JsonProperty("async_flag")
    var async = true
}