package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType.STRING
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class CreateArticleReplyReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "评论池id")
    @JsonProperty("article_id")
    var articleId: Long = 0

    @Protobuf(order = 2, required = true, fieldType = UINT64, description = "评论者用户uid")
    @JsonProperty("owner_uid")
    var ownerUID: Long = 0

    @Protobuf(order = 3, required = true, fieldType = STRING, description = "内容id")
    @JsonProperty("content")
    var content: String = ""

    @Protobuf(order = 4, required = false, fieldType = UINT64, description = "关联id")
    @JsonProperty("alias_id")
    var aliasId: Long = 0

}