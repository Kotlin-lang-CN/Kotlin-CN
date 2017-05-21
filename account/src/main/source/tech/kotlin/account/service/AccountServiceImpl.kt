package tech.kotlin.account.service

import com.relops.snowflake.Snowflake
import tech.kotlin.account.API.ACCOUNT
import tech.kotlin.account.dao.AccountDao
import tech.kotlin.account.dao.UserInfoDao
import tech.kotlin.common.algorithm.MD5
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.utils.prop
import tech.kotlin.common.utils.str
import tech.kotlin.model.Account
import tech.kotlin.model.EmptyResp
import tech.kotlin.model.UserInfo
import tech.kotlin.mysql.Mysql
import tech.kotlin.service.Node
import tech.kotlin.service.account.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountServiceImpl : AccountService {

    val properties: Properties = prop("account.properties")
    val passwordSalt: String = properties str "account.pwd.salt"
    val accountService: TokenService by lazy { Node[ACCOUNT][TokenService::class.java] }

    private fun encrypt(password: String) = MD5.hash("$password$passwordSalt")

    override fun create(req: CreateAccountReq): CreateAccountResp {
        //创建原始数据
        val current = System.currentTimeMillis()
        val account = Account().apply {
            id = Snowflake(0).next()
            password = encrypt(req.password)
            lastLogin = current
            state = Account.State.NORMAL
            role = Account.State.NORMAL
            createTime = current
        }
        val userInfo = UserInfo().apply {
            uid = account.id
            username = req.username
            logo = ""
            email = req.email
            emailState = UserInfo.EmailState.TO_BE_VERIFY
        }

        //注册事务
        Mysql.write {
            var user = UserInfoDao.getByName(it, req.username)
            if (user != null) abort(Err.USER_NAME_EXISTS)

            user = UserInfoDao.getByEmail(it, req.email)
            if (user != null) abort(Err.USER_EMAIL_EXISTS)

            AccountDao.saveOrUpdate(it, account)
            UserInfoDao.saveOrUpdate(it, userInfo)
        }

        return CreateAccountResp().apply {
            this.account = account
            this.token = accountService.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
        }
    }

    override fun loginWithName(req: LoginReq): LoginResp {
        var userInfo: UserInfo by Delegates.notNull<UserInfo>()
        //查询账号
        val account = Mysql.read {
            userInfo = UserInfoDao.getByName(it, req.loginName) ?: //查询缓存
                    UserInfoDao.getByEmail(it, req.loginName) ?: //查询数据库
                    abort(Err.USER_NOT_EXISTS)
            return@read Mysql.read {
                AccountDao.getById(it, id = userInfo.uid, useCache = false, updateCache = true)
            } ?: abort(Err.USER_NOT_EXISTS)
        }

        //校验密码
        if (account.password == req.password) abort(Err.ILLEGAL_PASSWORD)

        //执行登录, 更新登录时间，禁用缓存
        Mysql.write { AccountDao.saveOrUpdate(it, account.apply { lastLogin = System.currentTimeMillis() }) }

        return LoginResp().apply {
            this.token = accountService.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
        }
    }

    override fun freeze(req: FreezeAccountReq): EmptyResp {
        Mysql.write {
            val user = AccountDao.getById(it, req.uid) ?: abort(Err.UNAUTHORIZED)
            if (user.role != Account.Role.ADMIN) abort(Err.UNAUTHORIZED)

            req.opeation.forEach { uid, state ->
                AccountDao.update(it, uid, HashMap<String, Any>().apply { this["state"] = state })
            }
        }
        return EmptyResp()
    }



}