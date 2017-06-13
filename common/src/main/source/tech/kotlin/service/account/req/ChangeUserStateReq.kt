package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType.UINT32
import com.baidu.bjf.remoting.protobuf.FieldType.UINT64
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Account

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class ChangeUserStateReq {

    @Protobuf(order = 1, required = true, fieldType = UINT64, description = "operator uid")
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 2, required = true, fieldType = UINT32, description = "opereation")
    @JsonProperty("state")
    var state: Int = Account.State.NORMAL

}