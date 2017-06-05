package tech.kotlin.service.account

import com.fasterxml.jackson.annotation.JsonProperty
import com.relops.snowflake.Snowflake
import org.eclipse.jetty.http.HttpMethod
import tech.kotlin.dao.account.AccountDao
import tech.kotlin.dao.account.GithubUserInfoDao
import tech.kotlin.dao.account.UserInfoDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.GithubUser
import tech.kotlin.model.session.AccountSession
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.CreateAuthSessionReq
import tech.kotlin.model.request.GithubAuthReq
import tech.kotlin.model.response.CreateAuthSessionResp
import tech.kotlin.model.response.GithubAuthResp
import tech.kotlin.common.algorithm.JWT
import tech.kotlin.utils.Err
import tech.kotlin.utils.check
import tech.kotlin.utils.tryExec
import tech.kotlin.utils.Http
import tech.kotlin.utils.Mysql
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.long
import tech.kotlin.common.utils.str
import tech.kotlin.common.serialize.Json
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Githubs {

    private val properties: Properties = Props.loads("project.properties")
    private val jwtToken: String = properties str "github.jwt.token"
    private val jwtExpire: Long = properties long "github.jwt.expire"

    private val authHost: String = properties str "github.auth.host"
    private val clientId: String = properties str "github.auth.client.id"
    private val clientSecret: String = properties str "github.auth.client.secret"
    private val redirectUrl: String = properties str "github.auth.redirect.url"
    private val userUrl: String = properties str "github.user.url"

    fun createAuthSession(req: CreateAuthSessionReq): CreateAuthSessionResp {
        return CreateAuthSessionResp().apply {
            state = JWT.dumps(key = jwtToken, content = AccountSession().apply {
                id = Snowflake(0).next()
                device = req.device
                uid = 0
            })
        }
    }

    fun handleAuthCallback(req: GithubAuthReq): GithubAuthResp {
        tryExec(Err.GITHUB_AUTH_ERR) {
            val session = JWT.loads<AccountSession>(key = jwtToken, jwt = req.state, expire = jwtExpire)
            assert(session.device.isEquals(req.device))
        }
        val githubInfo = getUser(req.code, req.state)
        var account = Account()
        var userInfo = UserInfo()
        val needBind = Mysql.read {
            val result = GithubUserInfoDao.getById(it, id = githubInfo.id, useCache = true, updateCache = true)
            if (result == null || result.uid == 0L) return@read true
            userInfo = UserInfoDao.getById(it, result.uid, useCache = true, updateCache = true)!!
            account = AccountDao.getById(it, result.uid, useCache = true, updateCache = true)!!
            return@read false
        }
        return GithubAuthResp().apply {
            this.github = githubInfo
            this.hasAccount = !needBind
            this.userInfo = userInfo
            this.account = account
            this.sessionToken = JWT.dumps(key = jwtToken, content = AccountSession().apply {
                id = Snowflake(0).next()
                device = req.device
                uid = github.id
            })
        }
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

