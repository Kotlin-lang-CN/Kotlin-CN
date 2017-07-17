package tech.kotlin.service.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Message {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "消息id")
    @JsonProperty("id")
    var id = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "解析类型")
    @JsonProperty("type")
    var type = Type.ToArticle

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "时间戳")
    @JsonProperty("create_time")
    var createTime = 0L

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "消息体")
    @JsonProperty("content")
    var content = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.UINT64, description = "消息创建者id")
    @JsonProperty("creator")
    var createor = 0L

    @Protobuf(order = 6, required = true, fieldType = FieldType.UINT64, description = "消息接受者者id")
    @JsonProperty("acceptor")
    var acceptor = 0L

    @Protobuf(order = 7, required = true, fieldType = FieldType.UINT32, description = "消息状态")
    @JsonProperty("status")
    var status = Status.UNREAD

    object Status {
        const val UNREAD = 0
        const val READ = 1
    }

    object Type {
        const val ToArticle = 0
        const val ToReply = 1
    }

    object Group {
        const val ARTICLE = "a:%d"
    }

}