package tech.kotlin.common.utils

import com.relops.snowflake.Snowflake
import java.lang.management.ManagementFactory

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
internal val pid by lazy { ManagementFactory.getRuntimeMXBean().name.split("@")[0].toInt() }

object IDs : Snowflake(pid % 1024)