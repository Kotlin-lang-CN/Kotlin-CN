package cn.kotliner.forum.domain.model

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
enum class Category(val value: String) {
    QUESTION("问答"),
    ORIGINAL("原创"),
    REPOST("转载"),
    STATION("站务"),
    TRANSLATION("翻译"),
}