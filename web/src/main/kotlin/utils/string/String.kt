package utils.string

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import java.util.*

fun String.isEmailFormat() = EmailValidator().isValid(this, null) //Email 格式

fun String.isLettersOnly(): Boolean {
    for (c in this.toCharArray()) if (!Letters.contains(c)) return false
    return true
}

fun randStr(length: Int, sample: List<Char> = Letters.lowers): String {
    val sb = StringBuilder()
    val random = Random(System.currentTimeMillis())
    for (i in 1..length) sb.append(sample[random.nextInt(sample.size)])
    return sb.toString()
}

fun String.encrypt(key: String): String = Jwts.builder().setSubject(this)
        .signWith(SignatureAlgorithm.HS512, key).compact()

fun String.decrypt(key: String): String = Jwts.parser().setSigningKey(key)
        .parseClaimsJws(this).body.subject;

/***
 * Random String
 */
object Letters : ArrayList<Char>() {
    val lowers = Array(26, { (it + 97).toChar() }).toList()
    val uppers = Array(26, { (it + 65).toChar() }).toList()
    val digits = Array(10, { (it + 48).toChar() }).toList()

    init {
        addAll(lowers)
        addAll(uppers)
        addAll(digits)
    }
}