package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class UpdateArticleReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.MAP, description = "修改内容")
    @JsonProperty("args")
    var args: Map<String, String> = HashMap()

}