package tech.kotlin.service.account

import tech.kotlin.dao.account.AccountDao
import tech.kotlin.dao.account.UserInfoDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.model.request.UpdateUserReq
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.model.response.QueryUserResp
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object UserService {

    //通过id批量查询用户
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

    //更新用户信息
    fun updateById(req: UpdateUserReq): EmptyResp {
        if (req.args.isEmpty()) return EmptyResp()

        Mysql.write { session ->
            val args = HashMap<String, String>().apply { putAll(req.args) }

            if (args.containsKey("username")) {
                val user = UserInfoDao.getByName(session, args["username"]!!)
                if (user != null) abort(Err.USER_NAME_EXISTS)
            }
            if (args.containsKey("email")) {
                val user = UserInfoDao.getByEmail(session, args["email"]!!)
                if (user != null) abort(Err.USER_EMAIL_EXISTS)

                if (user?.email == args["email"]) {
                    args.remove("email")
                } else {
                    args["email_state"] = "${UserInfo.EmailState.TO_BE_VERIFY}"
                }
            }
            UserInfoDao.update(session, uid = req.id, args = args)
        }
        return EmptyResp()
    }
}



