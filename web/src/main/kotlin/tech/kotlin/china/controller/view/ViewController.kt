package tech.kotlin.china.controller.view

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Description
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import tech.kotlin.china.database.AccountMapper
import tech.kotlin.china.framework.Client
import tech.kotlin.china.framework.Database
import tech.kotlin.china.framework.JWT
import tech.kotlin.china.framework.of
import tech.kotlin.china.model.GithubAccount
import tech.kotlin.china.model.GithubTokenWrapper
import utils.dataflow.BusinessSafe
import utils.dataflow.monitor
import utils.dataflow.next
import utils.dataflow.require
import utils.json.toJson
import utils.map.p
import utils.map.toMap
import utils.properties.Env
import java.net.URLEncoder
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

const val SIGN_IN = "/account/github"

@Controller
class ViewController {

    val CLIENT_ID = Env["client_id"] ?: "ed1760e81a41e5553b0d"
    val CLIENT_SECRET = Env["client_secret"] ?: "<NULL>"
    val STATIC = Env["static"] ?: "http://static.kotlin-cn.tech"
    val HOST = Env["host"] ?: "http://kotlin-cn.tech"

    @Autowired lateinit var client: Client
    @Autowired lateinit var db: Database
    @Autowired lateinit var auth: JWT
    val log = Logger.getLogger("ViewController");

    fun app(page: String) = ModelAndView("template", p("static", STATIC).p("page", page))

    @RequestMapping("/article.html", method = arrayOf(GET)) fun article() = app("article")
    @RequestMapping("/community.html", method = arrayOf(GET)) fun community() = app("community")
    @RequestMapping("/document.html", method = arrayOf(GET)) fun doc() = app("document")
    @RequestMapping("/everyday.html", method = arrayOf(GET)) fun everyday() = app("everyday")
    @RequestMapping("/", method = arrayOf(GET)) fun voidIndex() = app("index")
    @RequestMapping("/index", method = arrayOf(GET)) fun index() = app("index")
    @RequestMapping("/publish.html", method = arrayOf(GET)) fun publish() = app("publish")

    @Description("github账号登录页面")
    @RequestMapping(SIGN_IN, method = arrayOf(GET))
    fun signInWithGithub(@RequestParam("code") code: String, response: HttpServletResponse,
                         @RequestHeader("Referer") referer: String,
                         @RequestHeader("Host") host: String,
                         @RequestParam("state") state: String): String {
        //请求数据的校验
        state.require("错误的状态码") { it.equals("kotlin_china") }
        //获得token
        val token = client.monitor("授权失败", 403) {
            it.post<GithubTokenWrapper>("https://github.com/login/oauth/access_token",
                    params = p("client_id", CLIENT_ID)
                            .p("client_secret", CLIENT_SECRET)
                            .p("code", code)
                            .p("redirect_uri", "$HOST$SIGN_IN")
                            .p("state", state))
        }.access_token
        //获得用户信息
        val profile = client.monitor("获取用户信息失败", 403) {
            it.get<GithubAccount>("https://api.github.com/user", params = p("access_token", token))
        }
        db write {
            val accountMapper = it.of<AccountMapper>()
            val account = accountMapper.queryById(profile.id)
            if (account == null) {
                accountMapper.addAccount(profile.toMap().p("token", token))
            } else {
                accountMapper.updateAccount(profile.toMap().p("token", token))
            }
        }
        response.addCookie(Cookie("kotlin_cn", URLEncoder.encode(
                p("token", auth.generateToken(profile)).p("profile", profile).toJson(), "UTF-8")
        ) next {
            it.maxAge = 7 * 24 * 60 * 60
            it.domain = if (host.contains(":")) host.substring(0, host.indexOf(":")) else host
            it.path = "/"
        })
        return "redirect:$referer"
    }

    @BusinessSafe
    @ExceptionHandler(Throwable::class)
    fun handleError(error: Throwable): ModelAndView {
        log.info("unhandled error", error)
        return app("index")
    }
}
