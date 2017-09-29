package cn.kotliner.forum.controller.account

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.Profile
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.*
import cn.kotliner.forum.service.account.req.*
import cn.kotliner.forum.service.account.resp.GithubCheckTokenReq
import cn.kotliner.forum.service.account.resp.LoginResp
import cn.kotliner.forum.service.article.req.EmailCheckTokenReq
import cn.kotliner.forum.utils.*
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/account")
class AccountController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var accountApi: AccountApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var profileApi: ProfileApi
    @Resource private lateinit var githubApi: GitHubApi
    @Resource private lateinit var emailApi: EmailApi

    //用户登录
    @PostMapping("/login")
    fun signIn(
            @RequestParam("github_token", defaultValue = "") githubToken: String,
            @NotBlank(message = "无效的用户名")
            @Length(min = 2, message = "用户名过短")
            @RequestParam("login_name") loginName: String,
            @NotBlank(message = "无效的密码")
            @Length(min = 8, message = "密码长度过短")
            @RequestParam("password") password: String
    ): Resp {
        val loginReq = LoginReq().apply {
            this.device = req.device
            this.loginName = loginName
            this.password = password
            if (!githubToken.isBlank()) {
                this.githubUser = githubApi.checkToken(GithubCheckTokenReq().apply {
                    this.token = githubToken
                    this.device = req.device
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
    fun signUp(
            @NotBlank(message = "无效的用户名")
            @Length(min = 2, message = "用户名过短")
            @RequestParam("username") username: String,
            @NotBlank(message = "无效的密码")
            @Length(min = 8, message = "密码长度过短")
            @RequestParam("password") password: String,
            @Email
            @RequestParam("email") email: String,
            @RequestParam("github_token", defaultValue = "") githubToken: String,
            @RequestParam("logo", defaultValue = "") logo: String
    ): Resp {
        //创建账号,并创建会话
        val createResp = accountApi.create(CreateAccountReq().apply {
            this.username = username
            this.password = password
            this.email = email
            this.device = req.device
            if (!githubToken.isBlank()) {
                this.githubUser = githubApi.checkToken(GithubCheckTokenReq().apply {
                    this.token = githubToken
                    this.device = req.device
                }).info
            }
        })

        //修改头像
        if (!logo.isBlank()) {
            userApi.updateById(UpdateUserReq().apply {
                this.id = createResp.account.id
                this.args = strDict { this["logo"] = logo }
            })
        }
        return ok {
            it["uid"] = createResp.account.id
            it["token"] = createResp.token
            it["logo"] = logo
        }
    }

    //修改密码
    @PostMapping("/user/{uid}/password")
    fun alterPassWord(
            @PathVariable("uid") uid: Long,
            @NotBlank(message = "无效的密码")
            @Length(min = 8, message = "密码长度过短")
            @RequestParam("password") password: String
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        accountApi.updatePassword(UpdatePasswordReq().apply {
            this.id = uid
            this.password = password
        })
        return ok()
    }

    //更新用户信息
    @PostMapping("/user/{uid}/update")
    fun alterUserInfo(
            @PathVariable("uid") uid: Long,
            @NotBlank(message = "无效的用户名")
            @Length(min = 2, message = "用户名过短")
            @RequestParam("username") username: String,
            @Email
            @RequestParam("email") email: String,
            @RequestParam("logo", defaultValue = "") logo: String
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.id == uid || it.role == Account.Role.ADMIN }

        userApi.updateById(UpdateUserReq().apply {
            this.id = uid
            this.args = strDict {
                if (!username.isBlank()) this["username"] = username
                if (!email.isBlank()) this["email"] = email
                if (!logo.isBlank()) this["logo"] = logo
            }
        })
        return ok()
    }

    //查询用户信息
    @GetMapping("/user/{uid}")
    fun getUserInfo(@PathVariable("uid") uid: Long): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN || it.id == uid }

        val queryUser = userApi.queryById(QueryUserReq().apply { id = arrayListOf(uid) })
        val info = queryUser.info[uid] ?: abort(Err.USER_NOT_EXISTS)
        val account = queryUser.account[uid] ?: abort(Err.SYSTEM)

        return ok {
            it["username"] = info.username
            it["email"] = info.email
            it["is_email_validate"] = (info.emailState == UserInfo.EmailState.VERIFIED)
            it["last_login"] = account.lastLogin
            it["state"] = account.state
            it["role"] = account.role
            it["create_time"] = account.createTime
            it["logo"] = info.logo
        }
    }

    //激活账户邮箱
    @GetMapping("/email/activate")
    fun activateEmail(@RequestParam("token") token: String): Resp {
        val resp = emailApi.checkToken(EmailCheckTokenReq().apply { this.token = token })
        userApi.activateEmail(ActivateEmailReq().apply { this.uid = resp.uid;this.email = resp.email })
        return ok()
    }

    //更新用户公开资料
    @PostMapping("/profile/update")
    fun updateProfile(
            @RequestParam("uid") uid: Long,
            @RequestParam("gender") gender: Int,
            @RequestParam("github", defaultValue = "") github: String,
            @RequestParam("blog", defaultValue = "") blog: String,
            @RequestParam("company", defaultValue = "") company: String,
            @RequestParam("location", defaultValue = "") location: String,
            @RequestParam("description", defaultValue = "") description: String,
            @RequestParam("education", defaultValue = "") education: String
    ): Resp {
        val profile = Profile().apply {
            this.uid = uid
            this.gender = gender
            this.github = github
            this.blog = blog
            this.company = company
            this.location = location
            this.description = description
            this.education = education
        }

        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.id == profile.uid || it.role == Account.Role.ADMIN }

        profileApi.updateById(UpdateProfileReq().apply { this.profile = arrayListOf(profile) })
        return ok()
    }

    //获取用户公开资料
    @GetMapping("/profile")
    fun getProfile(@RequestParam("id") queryIds: String): Resp {
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