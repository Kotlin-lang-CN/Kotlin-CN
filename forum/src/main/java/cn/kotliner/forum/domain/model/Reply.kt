package cn.kotliner.forum.domain.model

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Reply {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "回复id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "评论池id")
    @JsonProperty("reply_pool_id")
    var replyPoolId: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "评论者用户uid")
    @JsonProperty("owner_uid")
    var ownerUID: Long = 0

    @Protobuf(order = 4, required = true, fieldType = FieldType.UINT64, description = "创建时间")
    @JsonProperty("create_time")
    var createTime: Long = 0

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT32, description = "回复状态")
    @JsonProperty("state")
    var state: Int = State.NORMAL

    @Protobuf(order = 6, required = true, fieldType = FieldType.UINT64, description = "内容id")
    @JsonProperty("content_id")
    var contentId: Long = 0

    @Protobuf(order = 7, required = false, fieldType = FieldType.UINT64, description = "关联id")
    @JsonProperty("alias_id")
    var aliasId: Long = 0

    object State {
        const val NORMAL = 0
        const val BAN = 1
        const val DELETE = 2
    }

    object Pool {
        const val ARTICLE = "article"
    }
}