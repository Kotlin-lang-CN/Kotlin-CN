package cn.kotliner.forum.utils.algorithm

import java.math.BigInteger
import java.security.MessageDigest

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MD5 {

    fun hash(content: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(content.toByteArray())
        return BigInteger(1, md.digest()).toString(16)
    }

}