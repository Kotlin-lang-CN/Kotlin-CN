package tech.kotlin.service

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TypeDef {

    object Account {
        const val CREATE = 10000
        const val LOGIN_WITH_NAME = 10001
        const val CHANGE_USER_STATE = 10002
        const val UPDATE_PASSWORD = 10003
    }

    object Session {
        const val CREATE_SESSION = 20000
        const val CHECK_SESSION = 20001
    }

    object User {
        const val QUERY_BY_ID = 30000
        const val UPDATE_BY_ID = 30001
    }

    object Article {
        const val CREATE = 40000
        const val UPDATE_META = 40001
        const val UPDATE_CONTENT = 40002
        const val QUERY_BY_ID = 40003
        const val GET_LATEST = 40004
    }

    object Reply {
        const val CREATE = 50000
        const val CHANGE_STATE = 50001
        const val GET_REPLY_BY_ID = 50002
        const val GET_REPLY_BY_ARTICLE = 50003
        const val GET_REPLY_COUNT_BY_ARTICLE = 50004
    }

    object Text {
        const val GET_BY_ID = 60000
        const val CREATE_CONTENT = 60001
    }

    object EmailActivate {
        const val CREATE_SESSION = 70000
        const val ACTIVATE = 70001
    }

}