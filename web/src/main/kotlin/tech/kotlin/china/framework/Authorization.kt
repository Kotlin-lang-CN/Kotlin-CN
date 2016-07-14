package tech.kotlin.china.framework

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.kotlin.china.model.GithubAccount
import utils.dataflow.monitor
import utils.dataflow.next
import utils.json.toJson
import utils.json.toModel
import utils.properties.Env
import utils.string.decrypt
import utils.string.encrypt
import utils.string.randStr
import java.net.URLDecoder
import java.net.URLEncoder
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class JWT {

    @Autowired lateinit var request: HttpServletRequest;
    @Autowired lateinit var response: HttpServletResponse;
    val SECRET = Env["secret_jwt"]!!
    val AUTH_LOG = Env["log"]?.contains("authorization") == true

    fun login(profile: GithubAccount) {
        val jwt = WebToken(profile = profile, salt = randStr(64)).toJson().encrypt(SECRET)
        val host = request.getHeader("Host")
        response.addCookie(Cookie("kotlin_cn", URLEncoder.encode(
                CookieWrapper(token = jwt, profile = profile).toJson(), "UTF-8")
        ) next {
            it.maxAge = 7 * 24 * 60 * 60
            it.domain = if (host.contains(":")) host.substring(0, host.indexOf(":")) else host
            it.path = "/"
        })
    }

    fun loginRequire(): GithubAccount = request.monitor("未授权用户", 403, log = AUTH_LOG) {
        request.cookies.filter { it.name.equals("kotlin_cn") }
                .map {
                    URLDecoder.decode(it.value, "UTF-8").toModel(CookieWrapper::class)
                }
                .filter {
                    it.token.decrypt(SECRET).toModel(WebToken::class).profile.toJson()
                            .equals(it.profile.toJson())
                }
                .map { it.profile }[0]
    }

    class CookieWrapper(var token: String = "", var profile: GithubAccount = GithubAccount())
    class WebToken(var profile: GithubAccount = GithubAccount(), var salt: String = "")
}