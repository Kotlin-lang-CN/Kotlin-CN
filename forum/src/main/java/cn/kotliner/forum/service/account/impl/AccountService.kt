package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.utils.algorithm.MD5
import cn.kotliner.forum.dao.AccountRepository
import cn.kotliner.forum.dao.GitHubUserRepository
import cn.kotliner.forum.dao.UserRepository
import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.AccountApi
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.req.*
import cn.kotliner.forum.service.account.resp.CreateAccountResp
import cn.kotliner.forum.service.account.resp.LoginResp
import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.exceptions.abort
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@PropertySource("classpath:forum.properties")
class AccountService : AccountApi {

    @Value("\${account.pwd.salt}") private lateinit var passwordSalt: String
    @Value("\${admin.init.username}") private lateinit var initAdminUserName: String
    @Value("\${admin.init.password}") private lateinit var initAdminPassword: String
    @Value("\${admin.init.email}") private lateinit var initAdminEmail: String

    @Autowired private lateinit var sessionApi: SessionApi
    @Autowired private lateinit var userInfoRepo: UserRepository
    @Autowired private lateinit var accountRepo: AccountRepository
    @Autowired private lateinit var githubRepo: GitHubUserRepository
    @Autowired private lateinit var log: Logger

    //初始化管理员账号
    @Transactional(readOnly = false)
    override fun initAdmin() {
        val user = userInfoRepo.getByEmail(initAdminEmail)
        if (user != null) {
            log.info("admin $initAdminEmail has already exists!")
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
        accountRepo.saveOrUpdate(account)
        userInfoRepo.saveOrUpdate(UserInfo().apply {
            uid = account.id
            username = initAdminUserName
            logo = ""
            email = initAdminEmail
            emailState = UserInfo.EmailState.VERIFIED
        })
        log.info("init admin with name=$initAdminUserName, password=$initAdminPassword, email=$initAdminEmail")
    }

    //创建账号
    @Transactional(readOnly = false)
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
        var user = userInfoRepo.getByName(req.username)
        if (user != null) abort(Err.USER_NAME_EXISTS)

        user = userInfoRepo.getByEmail(req.email)
        if (user != null) abort(Err.USER_EMAIL_EXISTS)

        accountRepo.saveOrUpdate(account)
        userInfoRepo.saveOrUpdate(userInfo)
        //绑定github账号
        if (req.githubUser.id != 0L) {
            req.githubUser.uid = account.id
            githubRepo.saveOrUpdate(req.githubUser)
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
    @Transactional(readOnly = false)
    override fun loginWithName(req: LoginReq): LoginResp {
        val userInfo: UserInfo = userInfoRepo.getByName(req.loginName, updateCache = true) ?: //查询缓存
                userInfoRepo.getByEmail(req.loginName, updateCache = true) ?: //查询数据库
                abort(Err.USER_NOT_EXISTS)

        //查询账号
        val account = accountRepo.getById(id = userInfo.uid, useCache = false) ?:
                abort(Err.USER_NOT_EXISTS)

        //校验密码
        if (account.password != encrypt(req.password)) abort(Err.ILLEGAL_PASSWORD)

        //执行登录, 更新登录时间，禁用缓存, 绑定github账号
        if (req.githubUser.id != 0L) {
            githubRepo.saveOrUpdate(req.githubUser.apply { this.uid = account.id })
        }
        accountRepo.saveOrUpdate(account.apply { lastLogin = System.currentTimeMillis() })
        val currentAccount = accountRepo.getById(account.id, useCache = false)!!

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
    @Transactional(readOnly = false)
    override fun changeUserState(req: ChangeUserStateReq) {
        accountRepo.getById(req.uid, useCache = false) ?: abort(Err.UNAUTHORIZED)
        accountRepo.update(req.uid, hashMapOf("state" to "${req.state}"))
    }

    //修改用户密码
    @Transactional(readOnly = false)
    override fun updatePassword(req: UpdatePasswordReq) {
        accountRepo.update(req.id, hashMapOf("password" to encrypt(req.password)))
    }

    //获取当前用户总数
    @Transactional(readOnly = true)
    override fun getCount(): Int {
        return accountRepo.getCount()
    }


    private fun encrypt(password: String) = MD5.hash("$password$passwordSalt")

}