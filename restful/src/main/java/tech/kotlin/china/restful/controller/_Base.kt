package tech.kotlin.china.restful.controller

import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import tech.kotlin.china.restful.database.AccountMapper
import tech.kotlin.china.restful.database.use
import tech.kotlin.china.restful.framework.Config
import tech.kotlin.china.restful.framework.DBManager
import tech.kotlin.china.restful.model.BaseResponse
import tech.kotlin.china.restful.model.BusinessSafe
import tech.kotlin.china.restful.model.Fail
import tech.kotlin.china.restful.utils.*
import javax.servlet.http.HttpServletRequest

/***
 * Controller 基类
 *
 * 包含基本的请求逻辑过程(数据校验, 用户权限认证, 数据库会话)
 */
open class _Base {

    @Autowired lateinit var request: HttpServletRequest

    val tokenSecret = Config.get("token_secret")

    data class TokenWrapper(var uid: Long = 0, var salt: String = "", var admin: Boolean = false)

    /***
     * 生成用户的登录token
     */
    @BusinessSafe fun createToken(uid: Long, admin: Boolean = false) =
            TokenWrapper(uid = uid, salt = randStr(16), admin = admin).toJson().encrypt(tokenSecret)

    @BusinessSafe protected fun getUID(): Long? = getToken()?.uid//用负数代表管理员状态的uid

    protected fun getToken(): TokenWrapper? = safeMode {
        val token = request.getHeader("token") ?: request.cookies.findLast { it.name.equals("token") }!!.value
        val decrypt = token.decrypt(tokenSecret)
        decrypt.toModel(TokenWrapper::class.java)
    }

    /***
     * 请求格式检测
     */
    fun <T> Any.check(t: T, enable: Boolean = true, onCheck: (T) -> Unit) = if (enable) onCheck.invoke(t) else this

    /***
     * 登录判断
     */
    fun Any.authorized(login: Long? = null, admin: Boolean = false,
                       action: (SqlSession) -> Boolean = { true }): AuthorizedTask {
        val token = getToken()
                .require(message = "未登录用户", status = 403) { it != null }!!
                .require(message = "未授权用户", status = 403) { login == null || it.uid == login || it.admin == true }
        return object : _Base.AuthorizedTask {
            override fun onCheck(session: SqlSession): Boolean =
                    if (admin) session.use(AccountMapper::class.java) { it.seekRankOf(token.uid) == 1 }
                    else action.invoke(session)//对管理员权限执行检测
        }
    }

    interface AuthorizedTask {
        fun onCheck(session: SqlSession): Boolean
    }

    /***
     * 占用数据库会话的请求
     */
    fun Any.session(transaction: Boolean = false, action: (SqlSession) -> BaseResponse): BaseResponse =
            if (transaction) DBManager.dbWrite { session ->
                if (this is AuthorizedTask) require(message = "用户权限不足", status = 403) { this.onCheck(session) }
                return@dbWrite action.invoke(session)
            } else DBManager.dbRead { session ->
                if (this is AuthorizedTask) require(message = "用户权限不足", status = 403) { this.onCheck(session) }
                return@dbRead action.invoke(session)
            }

    /***
     * 执行业务逻辑的异常
     */
    @BusinessSafe @ExceptionHandler(BusinessError::class)
    fun handleError(error: BusinessError): Fail = Fail(status = error.status, message = error.msg)
}
