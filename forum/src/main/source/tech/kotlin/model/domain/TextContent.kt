package tech.kotlin.model.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class TextContent {

    @Protobuf(required = true, order = 1, fieldType = FieldType.UINT64, description = "id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(required = true, order = 2, fieldType = FieldType.STRING, description = "数据内容")
    @JsonProperty("content")
    var content: String = ""

    @Protobuf(required = true, order = 3, fieldType = FieldType.UINT32, description = "数据类型")
    @JsonProperty("type")
    var type: Int = Type.ARTICLE

    @Protobuf(required = true, order = 4, fieldType = FieldType.UINT64, description = "创建时间")
    @JsonProperty("create_time")
    var createTime: Long = 0

    @Protobuf(required = true, order = 5, fieldType = FieldType.UINT64, description = "关联id")
    @JsonProperty("alias_id")
    var aliasId: Long = 0

    object Type {
        const val ARTICLE = 0
        const val REPLY = 1
    }
}