package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class EmptyResp {

    @Protobuf(order = 1, required = false, fieldType = FieldType.UINT32, description = "状态值")
    @JsonProperty("status")
    var status: Int = 0

}