package cn.kotliner.forum.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class GithubUser {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64)
    @JsonProperty("uid")
    var uid = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.STRING)
    @JsonProperty("access_token")
    var accessToken = ""

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT64)
    @JsonProperty("id")
    var id = 0L

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING)
    @JsonProperty("name")
    var name = ""

    @Protobuf(order = 5, required = true, fieldType = FieldType.STRING)
    @JsonProperty("email")
    var email = ""

    @Protobuf(order = 6, required = true, fieldType = FieldType.STRING)
    @JsonProperty("avatar")
    var avatar = ""

    @Protobuf(order = 7, required = true, fieldType = FieldType.STRING)
    @JsonProperty("login")
    var login = ""

    @Protobuf(order = 8, required = true, fieldType = FieldType.STRING)
    @JsonProperty("blog")
    var blog = ""

    @Protobuf(order = 9, required = true, fieldType = FieldType.STRING)
    @JsonProperty("location")
    var location = ""

    @Protobuf(order = 10, required = true, fieldType = FieldType.UINT32)
    @JsonProperty("follower_count")
    var followerCount = 0

    @Protobuf(order = 11, required = true, fieldType = FieldType.STRING)
    @JsonProperty("company")
    var company = ""

}

