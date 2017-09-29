package cn.kotliner.forum.service.article.resp

import cn.kotliner.forum.domain.TextContent
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class QueryTextResp {

    @Protobuf(required = true, order = 1, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("result")
    var result: Map<Long, TextContent> = HashMap()

}