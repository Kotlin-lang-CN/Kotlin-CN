package tech.kotlin.china.restful.utils

import org.ocpsoft.prettytime.PrettyTime
import java.util.*

fun Date.format() = PrettyTime(Locale.CHINA).format(this)