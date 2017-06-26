package tech.kotlin.service.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Flower {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "点赞id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING, description = "点赞对象id")
    @JsonProperty("flower_pool_id")
    var flowerPoolId: String = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64, description = "点赞人")
    @JsonProperty("owner")
    var owner: Long = 0

    @Protobuf(order = 4, required = true, fieldType = FieldType.UINT64, description = "点赞时间(毫秒)")
    @JsonProperty("create_time")
    var createTime: Long = 0

}