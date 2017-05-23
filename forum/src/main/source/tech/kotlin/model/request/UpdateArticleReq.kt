package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class UpdateArticleReq {

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 1, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.UINT64, description = "文章id")
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    var id: Long = 0

    @com.baidu.bjf.remoting.protobuf.annotation.Protobuf(order = 2, required = true, fieldType = com.baidu.bjf.remoting.protobuf.FieldType.MAP, description = "修改内容")
    @com.fasterxml.jackson.annotation.JsonProperty("args")
    var args: Map<String, String> = HashMap()
}