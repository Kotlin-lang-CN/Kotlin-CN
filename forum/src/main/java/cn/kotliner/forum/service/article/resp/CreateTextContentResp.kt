package cn.kotliner.forum.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateTextContentResp {

    @Protobuf(required = true, order = 1, fieldType = FieldType.UINT64, description = "文本id")
    @JsonProperty("id")
    var id: Long = 0

}