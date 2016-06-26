package utils.date

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

fun Date.format() = PrettyTime(Locale.CHINA).format(this)

fun Date.format(format: SimpleDateFormat) = format.format(this)

fun Date.format(format: String) = format.format(SimpleDateFormat(format, Locale.CHINA))