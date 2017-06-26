package tech.kotlin.service.article.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Flower
import tech.kotlin.service.domain.Reply

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class QueryStarResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.BOOL, description = "查询结果")
    @JsonProperty("has_star")
    var hasStar = false

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "star信息")
    @JsonProperty("flower")
    var flower = Flower()

}