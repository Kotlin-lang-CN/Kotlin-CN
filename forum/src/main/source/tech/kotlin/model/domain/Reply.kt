package tech.kotlin.model.domain

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

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "评论类型")
    @JsonProperty("target_type")
    var targetType: Int = TargetType.ARTICLE

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "评论对象id")
    @JsonProperty("target_id")
    var targetId: Long = 0

    @Protobuf(order = 4, required = true, fieldType = FieldType.UINT64, description = "评论者用户uid")
    @JsonProperty("owner")
    var owner: Long = 0

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT64, description = "创建时间")
    @JsonProperty("create_time")
    var createTime: Long = 0

    @Protobuf(order = 6, required = false, fieldType = FieldType.UINT64, description = "上次编辑用户")
    @JsonProperty("last_edit_uid")
    var lastEditUIDr: Long = 0

    @Protobuf(order = 7, required = false, fieldType = FieldType.UINT64, description = "上次编辑时间")
    @JsonProperty("last_edit_time")
    var lastEditTime: Long = 0

    @Protobuf(order = 8, required = true, fieldType = FieldType.UINT32, description = "回复状态")
    @JsonProperty("state")
    var state: Int = State.NORMAL

    object TargetType {
        const val ARTICLE = 0
        const val REPLY = 1
        const val USER = 2
    }

    object State {
        const val NORMAL = 0
        const val BAN = 1
        const val DELETE = 2
    }
}