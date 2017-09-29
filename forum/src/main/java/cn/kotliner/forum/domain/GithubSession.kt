package cn.kotliner.forum.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class GithubSession {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "设备信息")
    @JsonProperty("device")
    var device: Device = Device()

    @Protobuf(order = 2, required = true, fieldType = FieldType.OBJECT, description = "用户id")
    @JsonProperty("github_user")
    var user: GithubUser = GithubUser()

}