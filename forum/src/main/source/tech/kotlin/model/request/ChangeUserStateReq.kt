package tech.kotlin.model.request

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.domain.Account
import java.util.HashMap

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class ChangeUserStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "operator uid")
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "opereation")
    @JsonProperty("state")
    var state: Int = Account.State.NORMAL

}