package tech.kotlin.controller

import spark.Route

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MiscController {

    val dashboard = Route { _, _ ->
        return@Route ok {
            it["text"] = """
            ## Kotlin-CN 社区 正式上线

            项目地址: [https://github.com/Kotlin-lang-CN](https://github.com/Kotlin-lang-CN)

            欢迎广大Kotlin爱好者参与讨论
            """.trimIndent()
        }
    }

}
