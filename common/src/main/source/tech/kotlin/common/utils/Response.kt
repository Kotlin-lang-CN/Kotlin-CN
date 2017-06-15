package tech.kotlin.common.utils

import spark.Route
import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
val property by lazy { Props.loads("project.properties") }
val crossSiteEnable by lazy { property bool "deploy.frontend.cross.site" }

fun ok(init: (HashMap<String, Any>) -> Unit = {}): String {
    val map = HashMap<String, Any>().apply { this["code"] = 0; this["msg"] = "" }
    init(map)
    return Json.dumps(map)
}

fun todo() = ok { it["code"] = -1; it["msg"] = "todo" }

fun Route.gate(desc: String, log: Boolean = true): Route {
    return Route { request, response ->
        val requestId = System.nanoTime()
        if (log) {
            val url = request.url()
            val method = request.requestMethod()
            val headers = request.headers().map { it to request.headers(it) }.toMap()
            val params = request.params()
            val queryParams = request.queryParams().map { it to request.queryParams(it) }.toMap()
            Log.d("Request", "$desc($requestId): ${Json.dumps(mapOf(
                    "url" to url,
                    "method" to method,
                    "headers" to headers,
                    "params" to params,
                    "query_params" to queryParams
            ))}")
        }
        var result: String
        if (crossSiteEnable) {
            response.header("Access-Control-Allow-Origin", "*")
            response.header("Access-Control-Allow-Credentials", "true")
            response.header("Access-Control-Allow-Headers",
                    "X-App-Device, X-App-Token, X-App-Platform, X-App-System, X-App-UID, X-App-Vendor")
            response.header("Access-Control-Allow-Methods", "GET, POST")
        }
        try {
            result = this.handle(request, response) as String
            if (log) Log.d("Response", "$desc($requestId): $result")
        } catch (err: Abort) {
            result = Json.dumps(err.model)
            if (log) Log.d("Response", err)
            if (log) Log.d("Response", "$desc($requestId): $result")
        } catch (err: Throwable) {
            result = Json.dumps(mapOf("code" to Err.SYSTEM.code, "msg" to Err.SYSTEM.msg))
            Log.e("Response", err)
        }
        return@Route result
    }
}