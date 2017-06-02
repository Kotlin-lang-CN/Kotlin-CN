package tech.kotlin.utils.serialize

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
fun dict(builder: HashMap<String, Any>.() -> Unit): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    builder(map)
    return map
}

fun strDict(builder: HashMap<String, String>.() -> Unit): HashMap<String, String> {
    val map = HashMap<String, String>()
    builder(map)
    return map
}