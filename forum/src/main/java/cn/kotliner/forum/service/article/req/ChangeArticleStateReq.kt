package cn.kotliner.forum.service.article.req

import cn.kotliner.forum.domain.model.Article
import com.baidu.bjf.remoting.protobuf.FieldType.UINT32
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

class ChangeArticleStateReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = UINT32, description = "修改封禁状态")
    @JsonProperty("state")
    var state: Int = Article.State.NORMAL

}