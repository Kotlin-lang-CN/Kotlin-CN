package cn.kotliner.forum.controller.account

import cn.kotliner.forum.domain.annotation.Login
import cn.kotliner.forum.domain.annotation.User
import cn.kotliner.forum.domain.form.*
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.Device
import cn.kotliner.forum.domain.model.Profile
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.*
import cn.kotliner.forum.service.account.req.*
import cn.kotliner.forum.service.account.resp.GithubCheckTokenReq
import cn.kotliner.forum.service.account.resp.LoginResp
import cn.kotliner.forum.service.article.req.EmailCheckTokenReq
import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import cn.kotliner.forum.utils.strDict
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource
import kotlin.collections.arrayListOf
import kotlin.collections.map
import kotlin.collections.set

@Validated
@RestController
@RequestMapping("/api/account")
class AccountController {

    @Resource private lateinit var accountApi: AccountApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var profileApi: ProfileApi
    @Resource private lateinit var githubApi: GitHubApi
    @Resource private lateinit var emailApi: EmailApi

    //用户登录
    @PostMapping("/login")
    @Validated
    fun signIn(
            @ModelAttribute form: LoginForm,
            @User device: Device
    ): Resp {
        val loginReq = LoginReq().apply {
            this.device = device
            this.loginName = form.login_name
            this.password = form.password
            if (!form.github_token.isBlank()) {
                this.githubUser = githubApi.checkToken(GithubCheckTokenReq().apply {
                    this.token = form.github_token
                    this.device = device
                }).info
            }
        }
        //创建登录会话
        val loginResp = accountApi.loginWithName(loginReq)
        //将第三方头像资料同步到用户头像
        val userLogo = object : (LoginReq, LoginResp) -> String {
            override fun invoke(req: LoginReq, resp: LoginResp): String {
                if (!req.githubUser.avatar.isBlank() && resp.userInfo.logo.isBlank()) {
                    userApi.updateById(UpdateUserReq().apply {
                        this.id = resp.account.id
                        this.args = strDict { this["logo"] = req.githubUser.avatar }
                    })
                }
                return if (req.githubUser.avatar.isBlank())
                    resp.userInfo.logo else req.githubUser.avatar
            }
        }(loginReq, loginResp)
        return ok {
            it["uid"] = loginResp.userInfo.uid
            it["token"] = loginResp.token
            it["username"] = loginResp.userInfo.username
            it["email"] = loginResp.userInfo.email
            it["is_email_validate"] = loginResp.userInfo.emailState == UserInfo.EmailState.VERIFIED
            it["role"] = loginResp.account.role
            it["logo"] = userLogo
        }
    }

    //用户注册
    @PostMapping("/register")
    @Validated
    fun signUp(
            @ModelAttribute form: RegisterForm,
            @User device: Device
    ): Resp {
        //创建账号,并创建会话
        val createResp = accountApi.create(CreateAccountReq().apply {
            this.username = form.username
            this.password = form.password
            this.email = form.email
            this.device = device
            if (!form.github_token.isBlank()) {
                this.githubUser = githubApi.checkToken(GithubCheckTokenReq().apply {
                    this.token = form.github_token
                    this.device = device
                }).info
            }
        })

        //修改头像
        if (!form.logo.isBlank()) {
            userApi.updateById(UpdateUserReq().apply {
                this.id = createResp.account.id
                this.args = strDict { this["logo"] = form.logo }
            })
        }

        return ok {
            it["uid"] = createResp.account.id
            it["token"] = createResp.token
            it["logo"] = form.logo
        }
    }

    //修改密码
    @PostMapping("/user/{uid}/password")
    @Validated
    @Login
    fun alterPassWord(
            @PathVariable("uid") uid: Long,
            @ModelAttribute form: AlterPwForm,
            @User account: Account
    ): Resp {
        account.check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        accountApi.updatePassword(UpdatePasswordReq().apply {
            this.id = uid
            this.password = form.password
        })
        return ok()
    }

    //更新用户信息
    @PostMapping("/user/{uid}/update")
    @Validated
    @Login
    fun alterUserInfo(
            @ModelAttribute form: AlterUserForm,
            @User account: Account
    ): Resp {
        account.check(Err.UNAUTHORIZED) { it.id == form.uid || it.role == Account.Role.ADMIN }

        userApi.updateById(UpdateUserReq().apply {
            this.id = form.uid
            this.args = strDict {
                if (!form.username.isBlank()) this["username"] = form.username
                if (!form.email.isBlank()) this["email"] = form.email
                if (!form.logo.isBlank()) this["logo"] = form.logo
            }
        })
        return ok()
    }

    //查询用户信息
    @GetMapping("/user/{uid}")
    @Login
    fun getUserInfo(
            @PathVariable("uid") uid: Long,
            @User account: Account
    ): Resp {
        account.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN || it.id == uid }

        val queryUser = userApi.queryById(QueryUserReq().apply { id = arrayListOf(uid) })
        val info = queryUser.info[uid] ?: abort(Err.USER_NOT_EXISTS)
        val queryAccount = queryUser.account[uid] ?: abort(Err.SYSTEM)

        return ok {
            it["username"] = info.username
            it["email"] = info.email
            it["is_email_validate"] = (info.emailState == UserInfo.EmailState.VERIFIED)
            it["last_login"] = queryAccount.lastLogin
            it["state"] = queryAccount.state
            it["role"] = queryAccount.role
            it["create_time"] = queryAccount.createTime
            it["logo"] = info.logo
        }
    }

    //激活账户邮箱
    @GetMapping("/email/activate")
    fun activateEmail(
            @RequestParam("token") token: String
    ): Resp {
        val resp = emailApi.checkToken(EmailCheckTokenReq().apply { this.token = token })
        userApi.activateEmail(ActivateEmailReq().apply { this.uid = resp.uid;this.email = resp.email })
        return ok()
    }

    //更新用户公开资料
    @PostMapping("/profile/update")
    @Validated
    @Login
    fun updateProfile(
            @ModelAttribute form: UpdateProfileForm,
            @User account: Account
    ): Resp {
        account.check(Err.UNAUTHORIZED) { it.id == form.uid || it.role == Account.Role.ADMIN }
        profileApi.updateById(UpdateProfileReq().apply {
            this.profile = arrayListOf(Profile().apply {
                this.uid = form.uid
                this.gender = form.gender
                this.github = form.github
                this.blog = form.blog
                this.company = form.company
                this.location = form.location
                this.description = form.description
                this.education = form.education
            })
        })
        return ok()
    }

    //获取用户公开资料
    @GetMapping("/profile")
    fun getProfile(
            @RequestParam("id") queryIds: String
    ): Resp {
        val id = queryIds.tryExec(Err.PARAMETER) { it.trim().split(",").map { it.toLong() } }
        val resp = profileApi.queryById(QueryUserReq().apply { this.id = id })
        return ok {
            it["profile"] = id.map { resp.profile[it] ?: Profile() }
        }
    }

    @GetMapping("/count")
    fun getUserCount(): Resp {
        return ok { it["total"] = accountApi.getCount() }
    }

}