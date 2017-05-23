package tech.kotlin.service.account

import tech.kotlin.dao.account.AccountDao
import tech.kotlin.dao.account.UserInfoDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.model.response.QueryUserResp
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object UserService {

    fun queryById(req: QueryUserReq): QueryUserResp {
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

