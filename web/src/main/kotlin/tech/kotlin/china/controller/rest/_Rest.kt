package tech.kotlin.china.controller.rest

import org.apache.ibatis.session.SqlSession
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException
import tech.kotlin.china.database.AccountMapper
import tech.kotlin.china.database.Database
import tech.kotlin.china.database.map
import tech.kotlin.china.utils.*
import java.net.BindException
import javax.servlet.http.HttpServletRequest

/***
 * Restful Controller
 *
 * 包含基本的请求逻辑过程函数(JWT编解码, 数据校验 check, 用户权限认证 authorized, 数据库会话 session)
 */
@RequestMapping("/rest")
open class _Rest {

    @Autowired lateinit var request: HttpServletRequest
    @Autowired lateinit var database: Database

    /***
     * 编码和解析 Json Web Token
     */
    val SECRET_JWT: String by lazy { Env["secret_jwt"] ?: "XPMQwiRS6aE8pHeUyVDotWCI92F50Ynu" }

    @BusinessSafe fun createToken(uid: Long, username: String, admin: Boolean = false) =
            TokenWrapper(uid = uid, salt = randStr(32), admin = admin, username = username)
                    .toJson().encrypt(SECRET_JWT)

    @BusinessSafe protected fun getUID(): Long? = getToken()?.uid//用负数代表管理员状态的uid

    protected fun getToken(): TokenWrapper? = try {
        val token = request.getHeader("token") ?: request.cookies.findLast { it.name.equals("token") }!!.value
        val decrypt = token.decrypt(SECRET_JWT)
        decrypt.toModel(TokenWrapper::class.java)
    } catch (e: Throwable) {
        null
    }

    data class TokenWrapper(var uid: Long = 0, var salt: String = "", var admin: Boolean = false,
                            var username: String = "")

    /***
     * 表单校验
     */

    fun <R> R.check(enable: Boolean = true, onCheck: (R) -> Unit): _Rest {
        if (enable) onCheck.invoke(this)
        return this@_Rest
    }

    /***
     * 用户权限相关的判断
     */
    fun authorized(login: Long? = null, admin: Boolean = false, strict: Boolean = false,
                   action: (SqlSession) -> Unit = { }): AuthorizedTask {
        val token = getToken()
                .require(message = "未登录用户", status = 403) { it != null }!!
                .require(message = "未授权用户", status = 403) { it.admin || login == null || it.uid == login }
        return object : AuthorizedTask {
            //对管理员权限执行检测
            override fun onCheck(session: SqlSession) {
                if (admin) {
                    session.map<AccountMapper>().seekRankOf(token.uid)
                            .require("用户权限不足", 403) { it == 1 }
                    if (strict) action.invoke(session)
                } else {
                    action.invoke(session)
                }
            }
        }
    }

    interface AuthorizedTask {
        fun onCheck(session: SqlSession)
    }

    /***
     * 数据库会话, 主要的逻辑方法:
     * 返回值存在数据则执行数据响应,否则执行成功响应
     */
    fun Any.session(transaction: Boolean = false, action: (SqlSession) -> Any): Map<String, Any?> = try {
        val doResponse: (SqlSession) -> Map<String, Any?> = {
            if (this is AuthorizedTask) onCheck(it) //如果存在身份校验,则先执行身份校验
            val result = action(it) //执行响应
            val response = p("status", 200).p("message", "")
            if (result is Unit) response else response.p("data", result) //如果响应有数据,则将数据打包到响应中去
        }
        if (transaction) database.dbWrite(doResponse) else database.dbRead(doResponse)
    } catch(e: Throwable) {
        if (e is BusinessError) throw e
        e.printStackTrace()
        throw BusinessError("服务器错误", 502)
    }

    /***
     * 执行业务逻辑的异常的响应
     */
    @BusinessSafe
    @ExceptionHandler(Throwable::class)
    open fun handleError(error: Throwable): Map<String, Any?> = when (error) {
        is BusinessError -> p("status", error.status).p("message", error.msg)
        is BindException -> p("status", 400).p("message", error.message)
        is ConversionNotSupportedException -> p("status", 500).p("message", error.message)
        is HttpMediaTypeNotAcceptableException -> p("status", 406).p("message", error.message)
        is HttpMediaTypeNotSupportedException -> p("status", 415).p("message", error.message)
        is HttpMessageNotReadableException -> p("status", 400).p("message", error.message)
        is HttpMessageNotWritableException -> p("status", 500).p("message", error.message)
        is HttpRequestMethodNotSupportedException -> p("status", 405).p("message", error.message)
        is MethodArgumentNotValidException -> p("status", 400).p("message", error.message)
        is MissingServletRequestParameterException -> p("status", 400).p("message", error.message)
        is MissingServletRequestPartException -> p("status", 400).p("message", error.message)
        is NoHandlerFoundException -> p("status", 404).p("message", error.message)
        is NoSuchRequestHandlingMethodException -> p("status", 404).p("message", error.message)
        is TypeMismatchException -> p("status", 400).p("message", error.message)
        is MissingPathVariableException -> p("status", 500).p("message", error.message)
        is NoHandlerFoundException -> p("status", 404).p("message", error.message)
        else -> p("stauts", 503).p("message", "未知服务器错误")
    }
}
