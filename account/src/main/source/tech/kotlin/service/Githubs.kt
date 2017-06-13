package tech.kotlin.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.eclipse.jetty.http.HttpMethod
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.GithubUser
import tech.kotlin.service.domain.AccountSession
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.account.req.GithubCreateStateReq
import tech.kotlin.service.account.req.GithubAuthReq
import tech.kotlin.service.account.resp.GithubCreateStateResp
import tech.kotlin.service.account.resp.GithubAuthResp
import tech.kotlin.common.algorithm.JWT
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.dao.AccountDao
import tech.kotlin.dao.GithubUserInfoDao
import tech.kotlin.dao.UserInfoDao
import tech.kotlin.service.account.resp.GithubCheckTokenReq
import tech.kotlin.service.account.resp.CheckCheckTokenResp
import tech.kotlin.service.account.GithubApi
import tech.kotlin.utils.*
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Githubs : GithubApi {

    private val properties: Properties = Props.loads("project.properties")
    private val jwtToken: String = properties str "github.jwt.token"
    private val jwtExpire: Long = properties long "github.jwt.expire"

    private val authHost: String = properties str "github.auth.host"
    private val clientId: String = properties str "github.auth.client.id"
    private val clientSecret: String = properties str "github.auth.client.secret"
    private val redirectUrl: String = properties str "github.auth.redirect.url"
    private val userUrl: String = properties str "github.user.url"

    override fun createState(req: GithubCreateStateReq): GithubCreateStateResp {
        return GithubCreateStateResp().apply {
            this.state = JWT.dumps(key = jwtToken, content = AccountSession().apply {
                id = IDs.next()
                device = req.device
                uid = 0
            })
        }
    }

    override fun createSession(req: GithubAuthReq): GithubAuthResp {
        tryExec(Err.GITHUB_AUTH_ERR, "无效的code") {
            val session = JWT.loads<AccountSession>(key = jwtToken, jwt = req.state, expire = jwtExpire)
            assert(session.device.isEquals(req.device))
        }
        val githubInfo = getUser(req.code, req.state)
        var account = Account()
        var userInfo = UserInfo()
        val hasAccount = Mysql.read {
            val result = GithubUserInfoDao.getById(it, id = githubInfo.id, useCache = true, updateCache = true)
            if (result == null || result.uid == 0L) return@read false
            userInfo = UserInfoDao.getById(it, result.uid, useCache = true, updateCache = true)!!
            account = AccountDao.getById(it, result.uid, useCache = true, updateCache = true)!!
            return@read true
        }
        return GithubAuthResp().apply {
            this.github = githubInfo
            this.hasAccount = hasAccount
            this.userInfo = userInfo
            this.account = account
            this.token = JWT.dumps(key = jwtToken, content = AccountSession().apply {
                id = IDs.next()
                device = req.device
                uid = github.id
            })
        }
    }

    override fun checkToken(req: GithubCheckTokenReq): CheckCheckTokenResp {
        TODO()
    }

    private fun getUser(code: String, state: String): GithubUser {
        val token = Json.loads<GithubAccessTokenResp>(Http.newRequest(authHost)
                .method(HttpMethod.GET)
                .header("Accept", "application/json")
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("code", code)
                .param("redirect_url", redirectUrl)
                .param("state", state)
                .send()
                .contentAsString).token
                .check(Err.GITHUB_AUTH_ERR) { it.isNullOrBlank() }

        val info = Json.loads<GithubUserResp>(Http.newRequest(userUrl)
                .method(HttpMethod.GET)
                .param("access_token", token)
                .send()
                .contentAsString)
                .check(Err.GITHUB_AUTH_ERR) { it.id != 0L }

        return GithubUser().apply {
            this.accessToken = token
            this.id = info.id
            this.name = info.name
            this.email = info.name
            this.avatar = info.avatar
            this.login = info.login
            this.blog = info.blog
            this.location = info.location
            this.followerCount = info.followers
            this.company = info.company
        }
    }

    class GithubAccessTokenResp {
        @JsonProperty("access_token") var token = ""
    }

    class GithubUserResp {
        @JsonProperty("login") var login = ""
        @JsonProperty("id") var id = 0L
        @JsonProperty("avatar_url") var avatar = ""
        @JsonProperty("url") var url = ""
        @JsonProperty("html_url") var html = ""
        @JsonProperty("name") var name = ""
        @JsonProperty("company") var company = ""
        @JsonProperty("blog") var blog = ""
        @JsonProperty("location") var location = ""
        @JsonProperty("email") var email = ""
        @JsonProperty("bio") var bio = ""
        @JsonProperty("public_repos") var publicRepos = ""
        @JsonProperty("followers") var followers = 0
        @JsonProperty("following") var following = 0
        @JsonProperty("created_at") var createTime = ""
    }
}

