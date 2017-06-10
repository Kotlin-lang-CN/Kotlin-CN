package tech.kotlin.service.account

import tech.kotlin.common.algorithm.MD5
import tech.kotlin.common.os.Log
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.str
import tech.kotlin.dao.account.AccountDao
import tech.kotlin.dao.account.UserInfoDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.*
import tech.kotlin.model.response.CreateAccountResp
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.model.response.LoginResp
import tech.kotlin.utils.Err
import tech.kotlin.utils.IDs
import tech.kotlin.utils.Mysql
import tech.kotlin.utils.abort
import java.util.*
import kotlin.properties.Delegates

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Accounts {

    private val properties: Properties = Props.loads("project.properties")
    private val passwordSalt: String = properties str "account.pwd.salt"
    private val initAdminUserName: String = properties str "admin.init.username"
    private val initAdminPassword: String = properties str "admin.init.password"
    private val initAdminEmail: String = properties str "admin.init.email"
    private fun encrypt(password: String) = MD5.hash("$password$passwordSalt")

    //初始化管理员账号
    fun initAdmin() {
        Mysql.write {
            val user = UserInfoDao.getByEmail(it, initAdminEmail)
            if (user != null) {
                Log.i("admin $initAdminEmail has already exists!")
                return
            }
            val current = System.currentTimeMillis()
            val account = Account().apply {
                id = IDs.next()
                password = encrypt(initAdminPassword)
                lastLogin = current
                state = Account.State.NORMAL
                role = Account.Role.ADMIN
                createTime = current
            }
            AccountDao.saveOrUpdate(it, account)
            UserInfoDao.saveOrUpdate(it, UserInfo().apply {
                uid = account.id
                username = initAdminUserName
                logo = ""
                email = initAdminEmail
                emailState = UserInfo.EmailState.VERIFIED
            })
            Log.i("init admin with name=$initAdminUserName, password=$initAdminPassword, email=$initAdminEmail")
        }
    }

    //创建账号
    fun create(req: CreateAccountReq): CreateAccountResp {
        val current = System.currentTimeMillis()
        val account = Account().apply {
            id = IDs.next()
            password = encrypt(req.password)
            lastLogin = current
            state = Account.State.NORMAL
            role = Account.Role.NORMAL
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
            this.token = Sessions.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
        }
    }

    //使用用户名或邮箱登录
    fun loginWithName(req: LoginReq): LoginResp {
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
        if (account.password != encrypt(req.password)) abort(Err.ILLEGAL_PASSWORD)

        //执行登录, 更新登录时间，禁用缓存
        val currentAccount = Mysql.write {
            AccountDao.saveOrUpdate(it, account.apply { lastLogin = System.currentTimeMillis() })
            AccountDao.getById(it, account.id)!!
        }

        return LoginResp().apply {
            this.token = Sessions.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
            this.account = currentAccount
        }
    }

    //修改用户状态
    fun changeUserState(req: ChangeUserStateReq): EmptyResp {
        Mysql.write {
            AccountDao.getById(it, req.uid) ?: abort(Err.UNAUTHORIZED)
            AccountDao.update(it, req.uid, hashMapOf("state" to "${req.state}"))
        }
        return EmptyResp()
    }

    //修改用户密码
    fun updatePassword(req: UpdatePasswordReq): EmptyResp {
        Mysql.write {
            AccountDao.update(it, req.id, hashMapOf("password" to encrypt(req.password)))
        }
        return EmptyResp()
    }

}

