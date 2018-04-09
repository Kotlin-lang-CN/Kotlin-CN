package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.utils.algorithm.JWT
import cn.kotliner.forum.dao.AccountRepository
import cn.kotliner.forum.dao.GitHubUserRepository
import cn.kotliner.forum.dao.UserRepository
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.GithubSession
import cn.kotliner.forum.domain.model.GithubUser
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.GitHubApi
import cn.kotliner.forum.service.account.req.GithubAuthReq
import cn.kotliner.forum.service.account.req.GithubCreateStateReq
import cn.kotliner.forum.service.account.resp.GithubAuthResp
import cn.kotliner.forum.service.account.resp.GithubCheckTokenReq
import cn.kotliner.forum.service.account.resp.GithubCheckTokenResp
import cn.kotliner.forum.service.account.resp.GithubCreateStateResp
import cn.kotliner.forum.utils.algorithm.Json
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Service
class GitHubService : GitHubApi {

    @Value("\${github.jwt.token}") private lateinit var jwtToken: String
    @Value("\${github.jwt.expire}") private lateinit var jwtExpire: String
    @Value("\${github.state.expire}") private lateinit var stateExpire: String

    @Value("\${github.auth.host}") private lateinit var authHost: String
    @Value("\${github.auth.client.id}") private lateinit var clientId: String
    @Value("\${github.auth.client.secret}") private lateinit var clientSecret: String
    @Value("\${github.user.url}") private lateinit var userUrl: String
    @Value("\${deploy.frontend.host}") private lateinit var frontendHost: String

    @Resource private lateinit var redis: StringRedisTemplate
    @Resource private lateinit var githubRepo: GitHubUserRepository
    @Resource private lateinit var accountRepo: AccountRepository
    @Resource private lateinit var userRepo: UserRepository

    private val rest = RestTemplate()

    override fun createState(req: GithubCreateStateReq): GithubCreateStateResp {
        val state = RandomStringUtils.randomAlphanumeric(32)
        val key = "github:state:${req.device.token}"
        redis.boundValueOps(key).set(state)
        redis.expire(key, stateExpire.toLong(), TimeUnit.MILLISECONDS)
        return GithubCreateStateResp().apply { this.state = state }
    }

    @Transactional(readOnly = false)
    override fun createSession(req: GithubAuthReq): GithubAuthResp {

        val key = "github:state:${req.device.token}"

        if (redis.boundValueOps(key).get() == null)
            abort(Err.GITHUB_AUTH_ERR, "无效的state")

        val githubInfo = getUser(req.code, req.state)

        val result = githubRepo.getById(id = githubInfo.id, useCache = true, updateCache = true)

        return GithubAuthResp().apply {

            this.hasAccount = result != null && result.uid != 0L

            this.userInfo = if (hasAccount) UserInfo() else userRepo.getById(result!!.uid, useCache = true)!!

            this.account = if (hasAccount) Account() else accountRepo.getById(result!!.uid, useCache = true)!!

            this.github = githubInfo

            this.token = JWT.dumps(key = jwtToken, content = GithubSession().apply {
                device = req.device
                user = githubInfo
            })
        }
    }

    override fun checkToken(req: GithubCheckTokenReq): GithubCheckTokenResp {
        val session = tryExec(Err.GITHUB_AUTH_ERR, "无效的token") {
            val session = JWT.loads<GithubSession>(key = jwtToken, jwt = req.token,
                    expire = jwtExpire.toLong())
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
        val tokenResp = rest.exchange(authHost, HttpMethod.GET,
                HttpEntity<Any>(object : () -> MultiValueMap<String, String> {
                    override fun invoke(): MultiValueMap<String, String> {
                        val result = LinkedMultiValueMap<String, String>()
                        result["Accept"] = "application/json"
                        result["client_id"] = clientId
                        result["client_secret"] = clientSecret
                        result["code"] = code
                        result["redirect_url"] = frontendHost
                        result["state"] = state
                        return result
                    }
                }()), GithubAccessTokenResp::class.java)


        val token = tokenResp.body.token.check(Err.GITHUB_AUTH_ERR) { !it.isBlank() }

        val infoResp = rest.exchange(userUrl, HttpMethod.GET,
                HttpEntity<Any>(object : () -> MultiValueMap<String, String> {
                    override fun invoke(): MultiValueMap<String, String> {
                        val result = LinkedMultiValueMap<String, String>()
                        result["Accept"] = "application/json"
                        result["access_token"] = token
                        return result
                    }
                }()), GithubUserResp::class.java)


        val info = infoResp.body.check(Err.GITHUB_AUTH_ERR) { it.id != 0L }

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
        @JsonProperty("access_token")
        var token = ""
    }

    class GithubUserResp {
        @JsonProperty("login")
        var login: String = ""
        @JsonProperty("id")
        var id = 0L
        @JsonProperty("avatar_url")
        var avatar: String? = ""
        @JsonProperty("name")
        var name: String? = ""
        @JsonProperty("company")
        var company: String? = ""
        @JsonProperty("blog")
        var blog: String? = ""
        @JsonProperty("location")
        var location: String? = ""
        @JsonProperty("email")
        var email: String? = ""
        @JsonProperty("followers")
        var followers = 0
    }
}
