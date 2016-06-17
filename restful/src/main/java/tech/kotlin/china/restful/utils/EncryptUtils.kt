package tech.kotlin.china.restful.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

/***
 * String Encrypt and Decrypt
 */
fun String.encrypt(key: String): String = Jwts.builder().setSubject(this)
        .signWith(SignatureAlgorithm.HS512, key).compact()

fun String.decrypt(key: String): String = Jwts.parser().setSigningKey(key)
        .parseClaimsJws(this).body.subject;

fun main(args: Array<String>) {
    val testKey = randStr(16);
    val test = randStr(255);
    println(test)
    println(test.encrypt(testKey))
    println(test.encrypt(testKey).decrypt(testKey))
}