package tech.kotlin.service.account.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.service.domain.Profile

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class UpdateProfileReq {

    @Protobuf(required = true, order = 1, fieldType = FieldType.OBJECT, description = "更新数据")
    @JsonProperty("profile")
    var profile: List<Profile> = ArrayList()

}