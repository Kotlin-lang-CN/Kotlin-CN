package tech.kotlin.account.service

import tech.kotlin.account.dao.AccountDao
import tech.kotlin.account.dao.UserInfoDao
import tech.kotlin.model.Account
import tech.kotlin.model.UserInfo
import tech.kotlin.mysql.Mysql
import tech.kotlin.service.account.QueryUserReq
import tech.kotlin.service.account.QueryUserResp
import tech.kotlin.service.account.UserService

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object UserServiceImpl : UserService {

    override fun queryById(req: QueryUserReq): QueryUserResp {
        if (req.id.isEmpty()) return QueryUserResp()

        val accountMap = HashMap<Long, Account>()
        val userInfoMap = HashMap<Long, UserInfo>()
        Mysql.read { session ->
            req.id.forEach { uid ->
                val account = AccountDao.getById(session, uid, useCache = true, updateCache = true)
                val userInfo = UserInfoDao.getById(session, uid, useCache = true, updateCache = true)
                if (account != null) accountMap[uid] = account
                if (userInfo != null) userInfoMap[uid] = userInfo
            }
        }
        return QueryUserResp().apply {
            account = accountMap;info = userInfoMap
        }
    }

}