package tech.kotlin.china.framework

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.kotlin.china.model.GithubAccount
import utils.dataflow.monitor
import utils.json.toJson
import utils.json.toModel
import utils.properties.Env
import utils.string.decrypt
import utils.string.encrypt
import utils.string.randStr
import javax.servlet.http.HttpServletRequest

@Service
class JWT {

    @Autowired lateinit var request: HttpServletRequest;
    val SECRET = Env["secret_jwt"] ?: "XPMQwiRS6aE8pHeUyVDotWCI92F50Ynu"

    fun loginRequire(): GithubAccount = request.monitor("未授权用户", 403) {
        it.getHeader("kotlin_china").decrypt(SECRET).toModel(WebToken::class).account
    }

    fun generateToken(account: GithubAccount): String =
            WebToken(account = account, salt = randStr(64)).toJson().encrypt(SECRET)

    class WebToken(val account: GithubAccount, val salt: String)
}