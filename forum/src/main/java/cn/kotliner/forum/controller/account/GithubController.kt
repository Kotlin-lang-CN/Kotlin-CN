package cn.kotliner.forum.controller.account

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.service.account.api.GitHubApi
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.req.CreateSessionReq
import cn.kotliner.forum.service.account.req.GithubAuthReq
import cn.kotliner.forum.service.account.req.GithubCreateStateReq
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/github")
class GithubController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var githubApi: GitHubApi
    @Resource private lateinit var sessionApi: SessionApi

    @GetMapping("/state")
    fun createState() = ok {
        it["state"] = githubApi.createState(GithubCreateStateReq().apply {
            this.device = req.device
        }).state
    }

    @PostMapping("/auth")
    fun auth(
            @RequestParam("code") code: String,
            @RequestParam("state") state: String
    ): Resp {
        val device = req.device
        val authResp = githubApi.createSession(GithubAuthReq().apply {
            this.device = device
            this.code = code
            this.state = state
        })

        if (!authResp.hasAccount) {
            return ok {
                it["need_create_account"] = true
                it["github_token"] = authResp.token
                it["github_email"] = authResp.github.email
                it["github_name"] = authResp.github.name
                it["github_avatar"] = authResp.github.avatar
            }
        } else {
            val token = sessionApi.createSession(CreateSessionReq().apply {
                this.device = device
                this.uid = authResp.account.id
            }).token
            return ok {
                it["need_create_account"] = false
                it["uid"] = authResp.account.id
                it["token"] = token
                it["username"] = authResp.userInfo.username
                it["email"] = authResp.userInfo.email
                it["is_email_validate"] = authResp.userInfo.emailState == UserInfo.EmailState.VERIFIED
                it["logo"] = authResp.userInfo.logo
                it["role"] = authResp.account.role
            }
        }
    }
}