package tech.kotlin.china.restful.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(format: String = "yyyy年MM月dd日 HH时mm分 E") = SimpleDateFormat(format, Locale.CHINA).format(this)