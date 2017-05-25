package tech.kotlin.model.domain

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import spark.Request
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Device() {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "设备token")
    @JsonProperty("token")
    var token: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "设备平台")
    @JsonProperty("platform")
    var platform: Int = Platform.DEFAULT

    @Protobuf(order = 3, required = true, fieldType = FieldType.STRING, description = "生产厂商")
    @JsonProperty("vendor")
    var vendor: String = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.STRING, description = "操作系统")
    @JsonProperty("system")
    var system: String = ""

    constructor(req: Request) : this() {
        token = (req.headers("X-App-Device") ?: "")
                .check(Err.PARAMETER, "缺失设备信息") { !it.isNullOrBlank() }

        platform = req.headers("X-App-Platform")?.apply {
            check(Err.PARAMETER, "缺失设备信息") { it.toInt();true }
        }?.toInt() ?: 0

        vendor = (req.headers("X-App-Vendor") ?: "")
                .check(Err.PARAMETER, "缺失设备信息") { !it.isNullOrBlank() }

        system = (req.headers("X-App-System") ?: "")
                .check(Err.PARAMETER, "缺失设备信息") { !it.isNullOrBlank() }
    }

    fun isEquals(device: Device): Boolean {
        return token == device.token
                && platform == device.platform
                && vendor == device.vendor
                && system == device.system
    }

    object Platform {
        const val DEFAULT = 0
        const val ANDROID = 1
        const val IOS = 2
    }
}