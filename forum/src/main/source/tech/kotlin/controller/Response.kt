package tech.kotlin.controller

import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
fun ok(init: (HashMap<String, Any>) -> Unit = {}): String {
    val map = HashMap<String, Any>().apply { this["code"] = 0; this["msg"] = "" }
    init(map)
    return Json.dumps(map)
}