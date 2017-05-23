package tech.kotlin.test.utils.algorithm

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.utils.algorithm.JWT
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class JWTTest {

    @Test
    fun testJWT() {
        for (sample in samples) {
            val key1 = testKeys[random.nextInt(testKeys.size)]
            val key2 = testKeys[random.nextInt(testKeys.size)]
            val jwt = JWT.dumps(key1, sample)
            try {
                val result = JWT.loads<SampleClass>(key2, jwt)
                assert(key1 == key2 && result.isEquals(sample))
            } catch (error: JWT.DecodeError) {
                assert(key1 != key2)
            }
        }
    }

    class SampleClass {
        @JsonProperty("prop_a")
        var a: Int = 0

        @JsonProperty("prop_b")
        var b: String = ""

        @JsonProperty("prop_c")
        var c: Long = 0

        @JsonProperty("prop_d")
        var d: SampleClass2 = SampleClass2()

        fun isEquals(other: SampleClass?): Boolean {
            return a == other?.a
                    && b == other.b
                    && c == other.c
                    && d.e == other.d.e
        }
    }

    class SampleClass2 {
        @JsonProperty("prop_e")
        var e: String = ""
    }

    companion object {

        val samples = ArrayList<SampleClass>()
        val testKeys = ArrayList<String>()
        val random = Random(System.currentTimeMillis())

        @JvmStatic
        @BeforeClass
        fun genTestSample() {
            val bytes = ByteArray(1024)
            for (i in 1..500) {
                samples.add(SampleClass().apply {
                    a = random.nextInt(1024)
                    random.nextBytes(bytes)
                    b = String(bytes)
                    c = random.nextLong()
                    random.nextBytes(bytes)
                    d = SampleClass2().apply { e = String(bytes) }
                })
            }
            for (i in 1..3) {
                random.nextBytes(bytes)
                val key = BigInteger(1024, SecureRandom(bytes)).toString(32)
                testKeys.add(key)
            }
        }
    }

}