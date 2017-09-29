package cn.kotliner.forum.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Profile {

    @Protobuf(order = 1, fieldType = FieldType.UINT64, required = true)
    @JsonProperty("uid")
    var uid: Long = 0L

    @Protobuf(order = 2, fieldType = FieldType.UINT32, required = true)
    @JsonProperty("gender")
    var gender: Int = Gender.MALE

    @Protobuf(order = 3, fieldType = FieldType.STRING, required = true)
    @JsonProperty("github")
    var github: String = ""

    @Protobuf(order = 4, fieldType = FieldType.STRING, required = true)
    @JsonProperty("blog")
    var blog: String = ""

    @Protobuf(order = 5, fieldType = FieldType.STRING, required = true)
    @JsonProperty("company")
    var company: String = ""

    @Protobuf(order = 6, fieldType = FieldType.STRING, required = true)
    @JsonProperty("location")
    var location: String = ""

    @Protobuf(order = 7, fieldType = FieldType.STRING, required = true)
    @JsonProperty("description")
    var description: String = ""

    @Protobuf(order = 8, fieldType = FieldType.STRING, required = true)
    @JsonProperty("education")
    var education: String = ""

    object Gender {
        const val MALE = 0
        const val FEMALE = 1
    }
}