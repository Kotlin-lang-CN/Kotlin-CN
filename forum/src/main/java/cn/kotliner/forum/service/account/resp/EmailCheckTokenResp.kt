package cn.kotliner.forum.service.account.resp

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class EmailCheckTokenResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64)
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING)
    @JsonProperty("email")
    var email: String = ""


}