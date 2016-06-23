package tech.kotlin.china.utils

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


/***
 * Random String
 */
object Letters : ArrayList<Char>() {
    val lowers = Array(26, { (it + 97).toChar() }).toList()
    val uppers = Array(26, { (it + 65).toChar() }).toList()
    val digits = Array(10, { (it + 48).toChar() }).toList()

    init {
        Letters.addAll(lowers)
        Letters.addAll(uppers)
        Letters.addAll(digits)
    }
}