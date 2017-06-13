package tech.kotlin.service

import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.account.req.UpdateUserReq
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.article.resp.QueryUserResp
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.dao.AccountDao
import tech.kotlin.dao.UserInfoDao
import tech.kotlin.service.account.UserApi
import tech.kotlin.utils.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Users : UserApi {

    //通过id批量查询用户
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

    //更新用户信息
    override fun updateById(req: UpdateUserReq): EmptyResp {
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



