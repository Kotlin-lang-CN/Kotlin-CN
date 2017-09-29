package cn.kotliner.forum.utils.gateway

typealias Resp = Map<String, Any>

fun ok(init: (HashMap<String, Any>) -> Unit = {}): Resp {
    val result = HashMap<String, Any>().apply {
        this["code"] = 0; this["msg"] = ""
    }
    init(result)
    return result
}

