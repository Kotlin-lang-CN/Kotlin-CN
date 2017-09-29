package cn.kotliner.forum.service.article.resp

import cn.kotliner.forum.domain.Reply
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryReplyByIdResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("result")
    var result: Map<Long, Reply> = HashMap()

}