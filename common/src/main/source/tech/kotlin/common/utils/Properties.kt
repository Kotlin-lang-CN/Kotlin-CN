package tech.kotlin.common.utils

import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 读取Properties
 *********************************************************************/
infix fun Properties.str(field: String): String = getProperty(field)

infix fun Properties.long(field: String) = getProperty(field).toLong()

infix fun Properties.int(field: String) = getProperty(field).toInt()