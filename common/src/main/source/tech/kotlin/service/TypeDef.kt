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
        const val ACTIVATE_EMAIL = 30002
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

    object Email {
        const val CREATE_SESSION = 70000
        const val CHECK_TOKEN = 70001
        const val SEND = 70002
    }

    object Github {
        const val CREATE_STATE = 80000
        const val CREATE_SESSION = 80001
        const val CHECK_TOKEN = 80002
    }

    object Flower {
        const val STAR = 90000
        const val UNSTAR = 90001
        const val COUNT_STAR = 90002
    }

}