package tech.kotlin.service.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Article {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "标题")
    @JsonProperty("title")
    var title: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "作者uid")
    @JsonProperty("author")
    var author: Long = 0

    @Protobuf(order = 4, required = true, fieldType = FieldType.UINT64, description = "创建时间(毫秒)")
    @JsonProperty("create_time")
    var createTime: Long = 0

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT32, description = "文章分类id")
    @JsonProperty("category")
    var category: Int = 0

    @Protobuf(order = 6, required = false, fieldType = FieldType.STRING, description = "标签")
    @JsonProperty("tags")
    var tags: String = ""

    @Protobuf(order = 7, required = false, fieldType = FieldType.UINT64, description = "上次编辑时间(毫秒)")
    @JsonProperty("last_edit_time")
    var lastEdit: Long = 0

    @Protobuf(order = 8, required = false, fieldType = FieldType.UINT64, description = "上册编辑者uid")
    @JsonProperty("last_edit_uid")
    var lastEditUID: Long = 0

    @Protobuf(order = 9, required = true, fieldType = FieldType.UINT32, description = "文章状态")
    @JsonProperty("state")
    var state: Int = State.NORMAL

    @Protobuf(order = 10, required = true, fieldType = FieldType.UINT64, description = "文章内容")
    @JsonProperty("content_id")
    var contentId: Long = 0

    object State {
        const val NORMAL = 0
        const val BAN = 1
        const val DELETE = 2
        const val FINE = 3
    }
}