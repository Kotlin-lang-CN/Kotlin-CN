package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.utils.ok
import tech.kotlin.utils.Redis

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MiscController {

    val dashboard = Route { _, _ -> return@Route ok { it["text"] = Redis.read { it["dashboard"] } } }

}
