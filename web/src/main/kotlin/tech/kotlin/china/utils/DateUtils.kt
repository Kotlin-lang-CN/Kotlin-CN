package tech.kotlin.china.utils

import org.ocpsoft.prettytime.PrettyTime
import java.util.*

fun Date.format() = PrettyTime(Locale.CHINA).format(this)