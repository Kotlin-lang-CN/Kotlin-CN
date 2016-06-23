package tech.kotlin.china.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

/***
 * String Encrypt and Decrypt
 */
fun String.encrypt(key: String): String = Jwts.builder().setSubject(this)
        .signWith(SignatureAlgorithm.HS512, key).compact()

fun String.decrypt(key: String): String = Jwts.parser().setSigningKey(key)
        .parseClaimsJws(this).body.subject;
