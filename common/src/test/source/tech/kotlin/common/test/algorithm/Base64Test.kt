package tech.kotlin.common.test.algorithm

import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.common.algorithm.Base64Utils.decode
import tech.kotlin.common.algorithm.Base64Utils.encode
import java.util.*
import kotlin.collections.ArrayList

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class Base64Test {

    @Test
    fun test() {
        for (sample in samples) {
            val encoded: String = encode(sample)
            val decoded: ByteArray = decode(encoded)
            println("origin:${sample.size}; encoded:$encoded; decoded:${decoded.size}")
            assert(Arrays.equals(sample, decoded))
        }
    }

    companion object {
        val samples = ArrayList<ByteArray>(100)

        @BeforeClass @JvmStatic
        fun genTestSample() {
            val random = Random(System.currentTimeMillis())
            for (i in 1..100) {
                val bytes = ByteArray(random.nextInt(4096) + 1)
                random.nextBytes(bytes)
                samples.add(bytes)
            }
        }
    }

}