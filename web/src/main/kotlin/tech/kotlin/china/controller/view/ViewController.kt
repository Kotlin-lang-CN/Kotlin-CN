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
import utils.dataflow.require
import utils.map.p
import utils.map.row
import utils.properties.Env

const val SIGN_IN = "/account/github"

@Controller
class ViewController {

    val CLIENT_ID = Env["client_id"]!!
    val CLIENT_SECRET = Env["client_secret"]!!
    val STATIC = Env["static"]!!
    val HOST = Env["host"]!!

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
    fun signInWithGithub(@RequestParam("code") code: String, @RequestHeader("Referer") referer: String,
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
            if (accountMapper.queryById(profile.id) == null) {
                accountMapper.addAccount(profile.row().p("token", token))
            } else {
                accountMapper.updateAccount(profile.row().p("token", token))
            }
        }
        auth.login(profile)
        return "redirect:$referer"
    }

    @BusinessSafe
    @ExceptionHandler(Throwable::class)
    fun handleError(error: Throwable): ModelAndView {
        log.info("unhandled error", error)
        return app("index")
    }
}
