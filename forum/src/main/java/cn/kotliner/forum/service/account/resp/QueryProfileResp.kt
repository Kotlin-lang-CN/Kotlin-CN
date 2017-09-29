package cn.kotliner.forum.service.account.resp

import cn.kotliner.forum.domain.Profile
import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class QueryProfileResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("profile")
    var profile: Map<Long, Profile> = HashMap()

}