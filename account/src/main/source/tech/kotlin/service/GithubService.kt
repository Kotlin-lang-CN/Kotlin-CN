package tech.kotlin.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.RandomStringUtils
import org.eclipse.jetty.http.HttpMethod
import tech.kotlin.common.algorithm.JWT
import tech.kotlin.common.os.Log
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.dao.AccountDao
import tech.kotlin.dao.GitHubUserInfoDao
import tech.kotlin.dao.UserInfoDao
import tech.kotlin.service.account.GithubApi
import tech.kotlin.service.account.req.GithubAuthReq
import tech.kotlin.service.account.req.GithubCreateStateReq
import tech.kotlin.service.account.resp.GithubAuthResp
import tech.kotlin.service.account.resp.GithubCheckTokenReq
import tech.kotlin.service.account.resp.GithubCheckTokenResp
import tech.kotlin.service.account.resp.GithubCreateStateResp
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.GithubSession
import tech.kotlin.service.domain.GithubUser
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.common.http.Http
import tech.kotlin.common.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GithubService : GithubApi {

    private val jwtToken: String = Props str "github.jwt.token"
    private val jwtExpire: Long = Props long "github.jwt.expire"
    private val stateExpire: Int = Props int "github.state.expire"

    private val authHost: String = Props str "github.auth.host"
    private val clientId: String = Props str "github.auth.client.id"
    private val clientSecret: String = Props str "github.auth.client.secret"
    private val userUrl: String = Props str "github.user.url"
    private val frontendHost: String = Props str "deploy.frontend.host"

    override fun createState(req: GithubCreateStateReq): GithubCreateStateResp {
        val state = RandomStringUtils.randomAlphanumeric(32)
        Redis {
            val key = "github:state:${req.device.token}"
            it[key] = state
            it.expire(key, stateExpire / 1000)
        }
        return GithubCreateStateResp().apply { this.state = state }
    }

    override fun createSession(req: GithubAuthReq): GithubAuthResp {
        Redis {
            val key = "github:state:${req.device.token}"
            if (it[key].isNullOrBlank()) abort(Err.GITHUB_AUTH_ERR, "无效的state")
        }
        val githubInfo = getUser(req.code, req.state)
        var account = Account()
        var userInfo = UserInfo()
        val hasAccount = Mysql.read {
            val result = GitHubUserInfoDao.getById(it, id = githubInfo.id, useCache = true, updateCache = true)
            if (result == null || result.uid == 0L) return@read false
            userInfo = UserInfoDao.getById(it, result.uid, useCache = true)!!
            account = AccountDao.getById(it, result.uid, useCache = true)!!
            return@read true
        }
        return GithubAuthResp().apply {
            this.hasAccount = hasAccount
            this.userInfo = userInfo
            this.account = account
            this.token = JWT.dumps(key = jwtToken, content = GithubSession().apply {
                device = req.device
                user = githubInfo
            })
        }
    }

    override fun checkToken(req: GithubCheckTokenReq): GithubCheckTokenResp {
        val session = tryExec(Err.GITHUB_AUTH_ERR, "无效的token") {
            val session = JWT.loads<GithubSession>(key = jwtToken, jwt = req.token, expire = jwtExpire)
            println(Json.dumps(session))
            assert(session.device.isEquals(req.device))
            assert(session.user.id != 0L)
            return@tryExec session
        }
        return GithubCheckTokenResp().apply {
            this.info = session.user
        }
    }


    private fun getUser(code: String, state: String): GithubUser {
        val tokenResp = Http.newRequest(authHost)
                .method(HttpMethod.GET)
                .header("Accept", "application/json")
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("code", code)
                .param("redirect_url", frontendHost)
                .param("state", state)
                .send()
                .contentAsString
        Log.i("GitHub", tokenResp)
        val token = Json.loads<GithubAccessTokenResp>(tokenResp).token
                .check(Err.GITHUB_AUTH_ERR) { !it.isNullOrBlank() }

        val infoResp = Http.newRequest(userUrl)
                .method(HttpMethod.GET)
                .param("access_token", token)
                .send()
                .contentAsString
        Log.i("GitHub", infoResp)
        val info = Json.loads<GithubUserResp>(infoResp)
                .check(Err.GITHUB_AUTH_ERR) { it.id != 0L }

        return GithubUser().apply {
            this.accessToken = token
            this.id = info.id
            this.name = info.name ?: ""
            this.email = info.name ?: ""
            this.avatar = info.avatar ?: ""
            this.login = info.login
            this.blog = info.blog ?: ""
            this.location = info.location ?: ""
            this.followerCount = info.followers
            this.company = info.company ?: ""
        }
    }

    class GithubAccessTokenResp {
        @JsonProperty("access_token") var token = ""
    }

    class GithubUserResp {
        @JsonProperty("login") var login: String = ""
        @JsonProperty("id") var id = 0L
        @JsonProperty("avatar_url") var avatar: String? = ""
        @JsonProperty("name") var name: String? = ""
        @JsonProperty("company") var company: String? = ""
        @JsonProperty("blog") var blog: String? = ""
        @JsonProperty("location") var location: String? = ""
        @JsonProperty("email") var email: String? = ""
        @JsonProperty("followers") var followers = 0
    }
}

