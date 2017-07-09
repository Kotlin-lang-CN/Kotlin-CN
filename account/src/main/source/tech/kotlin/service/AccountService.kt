package tech.kotlin.service

import tech.kotlin.common.algorithm.MD5
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.*
import tech.kotlin.dao.AccountDao
import tech.kotlin.dao.GitHubUserInfoDao
import tech.kotlin.dao.UserInfoDao
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.account.resp.CreateAccountResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.account.resp.LoginResp
import tech.kotlin.service.account.AccountApi
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.*
import kotlin.properties.Delegates

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AccountService : AccountApi {

    private val passwordSalt: String = Props str "account.pwd.salt"
    private val initAdminUserName: String = Props str "admin.init.username"
    private val initAdminPassword: String = Props str "admin.init.password"
    private val initAdminEmail: String = Props str "admin.init.email"
    private fun encrypt(password: String) = MD5.hash("$password$passwordSalt")

    val sessionApi by Serv.bind(SessionApi::class)

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
    override fun create(req: CreateAccountReq): CreateAccountResp {
        val current = System.currentTimeMillis()
        val newUID = IDs.next()
        val account = Account().apply {
            id = newUID
            password = encrypt(req.password)
            lastLogin = current
            state = Account.State.NORMAL
            role = Account.Role.NORMAL
            createTime = current
        }
        val userInfo = UserInfo().apply {
            uid = newUID
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
            //绑定github账号
            if (req.githubUser.id != 0L) {
                req.githubUser.uid = account.id
                GitHubUserInfoDao.saveOrUpdate(it, req.githubUser)
            }
        }

        return CreateAccountResp().apply {
            this.account = account
            this.token = sessionApi.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
        }
    }

    //使用用户名或邮箱登录
    override fun loginWithName(req: LoginReq): LoginResp {
        var userInfo: UserInfo by Delegates.notNull<UserInfo>()
        //查询账号
        val account = Mysql.read {
            userInfo = UserInfoDao.getByName(it, req.loginName, updateCache = true) ?: //查询缓存
                       UserInfoDao.getByEmail(it, req.loginName, updateCache = true) ?: //查询数据库
                       abort(Err.USER_NOT_EXISTS)
            return@read AccountDao.getById(it, id = userInfo.uid, useCache = false) ?:
                        abort(Err.USER_NOT_EXISTS)
        }

        //校验密码
        if (account.password != encrypt(req.password)) abort(Err.ILLEGAL_PASSWORD)

        //执行登录, 更新登录时间，禁用缓存
        val currentAccount = Mysql.write {
            //绑定github账号
            if (req.githubUser.id != 0L) {
                req.githubUser.uid = account.id
                GitHubUserInfoDao.saveOrUpdate(it, req.githubUser)
            }
            AccountDao.saveOrUpdate(it, account.apply { lastLogin = System.currentTimeMillis() })
            AccountDao.getById(it, account.id, useCache = false)!!
        }

        return LoginResp().apply {
            this.token = sessionApi.createSession(CreateSessionReq().apply {
                this.device = req.device
                this.uid = account.id
            }).token
            this.userInfo = userInfo
            this.account = currentAccount
        }
    }

    //修改用户状态
    override fun changeUserState(req: ChangeUserStateReq): EmptyResp {
        Mysql.write {
            AccountDao.getById(it, req.uid, useCache = false) ?: abort(Err.UNAUTHORIZED)
            AccountDao.update(it, req.uid, hashMapOf("state" to "${req.state}"))
        }
        return EmptyResp()
    }

    //修改用户密码
    override fun updatePassword(req: UpdatePasswordReq): EmptyResp {
        Mysql.write {
            AccountDao.update(it, req.id, hashMapOf("password" to encrypt(req.password)))
        }
        return EmptyResp()
    }

}


