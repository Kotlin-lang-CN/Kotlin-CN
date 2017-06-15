package tech.kotlin.service.domain

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
enum class Category(val value: String) {
    ORIGINAL("原创"),
    QUESTION("问答"),
    REPOST("转载"),
    STATION("站务"),
    TRANSLATION("翻译"),
}