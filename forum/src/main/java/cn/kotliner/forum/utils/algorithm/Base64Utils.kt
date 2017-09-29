package cn.kotliner.forum.utils.algorithm

import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Base64Utils {

    @Throws(Exception::class)
    fun decode(base64: String): ByteArray = Base64.getDecoder().decode(base64.toByteArray())

    @Throws(Exception::class)
    fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

}