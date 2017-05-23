package tech.kotlin.utils.algorithm

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.HashMap
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * RSA对称加密工具
 *********************************************************************/
object RSA {

    const private val KEY_ALGORITHM = "RSA"                 //加密算法RSA
    const private val SIGNATURE_ALGORITHM = "MD5withRSA"    //签名算法
    const private val PUBLIC_KEY = "RSAPublicKey"           //获取公钥的key
    const private val PRIVATE_KEY = "RSAPrivateKey"         //获取私钥的key
    const private val MAX_ENCRYPT_BLOCK = 117               //RSA最大加密明文大小
    const private val MAX_DECRYPT_BLOCK = 128               //RSA最大解密密文大小

    /**
     * 生成密钥对(公钥和私钥)
     */
    @Throws(Exception::class)
    fun genKeyPair(): Map<String, Any> {
        val keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyPairGen.initialize(1024)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val keyMap = HashMap<String, Any>(2)
        keyMap.put(PUBLIC_KEY, publicKey)
        keyMap.put(PRIVATE_KEY, privateKey)
        return keyMap
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun sign(data: ByteArray, privateKey: String): String {
        val keyBytes = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateK)
        signature.update(data)
        return Base64Utils.encode(signature.sign())
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     */
    @Throws(Exception::class)
    fun verify(data: ByteArray, publicKey: String, sign: String): Boolean {
        val keyBytes = Base64Utils.decode(publicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicK)
        signature.update(data)
        return signature.verify(Base64Utils.decode(sign))
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun decryptByPrivateKey(encryptedData: ByteArray, privateKey: String): ByteArray {
        val keyBytes = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateK)
        return doFinal(encryptedData, cipher, false)
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun decryptByPublicKey(encryptedData: ByteArray, publicKey: String): ByteArray {
        val keyBytes = Base64Utils.decode(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(x509KeySpec)
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, publicK)
        return doFinal(encryptedData, cipher, false)
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param N             公钥N
     * @param E             公钥e
     */
    @Throws(Exception::class)
    fun decryptByPublicKey(encryptedData: ByteArray, N: ByteArray, E: Int): ByteArray {
        val x509KeySpec = RSAPublicKeySpec(BigInteger(N), BigInteger.valueOf(E.toLong()))
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(x509KeySpec)
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, publicK)
        return doFinal(encryptedData, cipher, false)
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray, publicKey: String): ByteArray {
        val keyBytes = Base64Utils.decode(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(x509KeySpec)
        // 对数据加密
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        return doFinal(data, cipher, true)
    }

    /**
     * 公钥加密
     *
     * @param data 源数据
     * @param N    公钥N
     * @param E    公钥e
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray, N: ByteArray, E: Int): ByteArray {
        val x509KeySpec = RSAPublicKeySpec(BigInteger(N), BigInteger.valueOf(E.toLong()))
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(x509KeySpec)
        // 对数据加密
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        return doFinal(data, cipher, true)
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun encryptByPrivateKey(data: ByteArray, privateKey: String): ByteArray {
        val keyBytes = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, privateK)
        return doFinal(data, cipher, true)
    }

    /**
     * 获取私钥

     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPrivateKey(keyMap: Map<String, Any>): String {
        val key = keyMap[PRIVATE_KEY] as Key
        return Base64Utils.encode(key.encoded)
    }

    /**
     * 获取公钥

     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPublicKey(keyMap: Map<String, Any>): String {
        val key = keyMap[PUBLIC_KEY] as Key
        return Base64Utils.encode(key.encoded)
    }

    /**
     * 获取字节格式的公钥
     */
    fun getRawPublicKey(keyMap: Map<String, Any>): ByteArray {
        val key = keyMap[PUBLIC_KEY] as Key
        return key.encoded
    }

    fun getPublicKeyE(keyMap: Map<String, Any>): Int {
        val key = keyMap[PUBLIC_KEY] as RSAPublicKey
        return key.publicExponent.toInt()
    }

    fun getPublicKeyN(keyMap: Map<String, Any>): ByteArray {
        val key = keyMap[PUBLIC_KEY] as RSAPublicKey
        return key.modulus.toByteArray()
    }

    fun getRawPrivateKey(keyMap: Map<String, Any>): ByteArray {
        val key = keyMap[PRIVATE_KEY] as Key
        return key.encoded
    }

    /**
     * 用字节格式的私钥进行解密
     */
    @Throws(Exception::class)
    fun decryptByRawPrivateKey(encryptedData: ByteArray, keyMap: Map<String, Any>): ByteArray {
        val pkcs8KeySpec = PKCS8EncodedKeySpec((keyMap[PRIVATE_KEY] as Key).encoded)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateK)
        return doFinal(encryptedData, cipher, false)
    }

    @Throws(BadPaddingException::class, IllegalBlockSizeException::class, java.io.IOException::class)
    private fun doFinal(encryptedData: ByteArray, cipher: Cipher, isEncryptMode: Boolean): ByteArray {
        val inputLen = encryptedData.size
        ByteArrayOutputStream().use { out ->
            var offSet = 0
            var cache: ByteArray
            var i = 0
            // 对数据分段解密
            val block = if (isEncryptMode) MAX_ENCRYPT_BLOCK else MAX_DECRYPT_BLOCK
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > block) {
                    cache = cipher.doFinal(encryptedData, offSet, block)
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet)
                }
                out.write(cache, 0, cache.size)
                i++
                offSet = i * block
            }
            return out.toByteArray()
        }
    }
}