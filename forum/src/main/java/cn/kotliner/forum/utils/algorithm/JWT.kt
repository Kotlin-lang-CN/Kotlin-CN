package cn.kotliner.forum.utils.algorithm

import cn.kotliner.forum.utils.algorithm.encode.RC4Encoder
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 提供了一种对象对称加密方法，key为公钥，公钥要求长度范围为1-255
 *********************************************************************/
object JWT {

    private val random = Random(System.currentTimeMillis())

    fun dumps(key: String, content: Any): String {
        val json = Json.dumps(JWTWrapper().apply {
            timestamp = System.currentTimeMillis()
            random.nextBytes(salt)
            data = Json.dumps(content)
        })
        val bytes = json.toByteArray()
        val encrypted = RC4Encoder(key.toByteArray()).encode(bytes, 0, bytes.size)
        return Base64Utils.encode(encrypted)
    }

    @Throws(ExpiredError::class, DecodeError::class)
    inline fun <reified T : Any> loads(key: String, jwt: String, expire: Long = 0L): T {
        try {
            val bytes = Base64Utils.decode(jwt)
            val decrypted = RC4Encoder(key.toByteArray()).decode(bytes, 0, bytes.size)
            val wrapper = Json.loads<JWTWrapper>(String(decrypted))
            if (expire > 0L && System.currentTimeMillis() - wrapper.timestamp > expire)
                throw ExpiredError()
            return Json.loads<T>(wrapper.data)
        } catch (e: ExpiredError) {
            throw e
        } catch (e: Exception) {
            throw DecodeError(e)
        }
    }

    class JWTWrapper {
        @JsonProperty("timestamp") var timestamp: Long = 0
        @JsonProperty("salt") var salt: ByteArray = ByteArray(16)
        @JsonProperty("data") var data: String = ""
    }

    class ExpiredError : RuntimeException("token expired")
    class DecodeError(e: Exception) : RuntimeException(e)
}