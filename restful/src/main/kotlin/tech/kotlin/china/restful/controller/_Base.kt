package tech.kotlin.china.restful.controller

import org.apache.ibatis.session.SqlSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import tech.kotlin.china.restful.database.AccountMapper
import tech.kotlin.china.restful.database.get
import tech.kotlin.china.restful.framework.Config
import tech.kotlin.china.restful.framework.DBManager
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
            TokenWrapper(uid = uid, salt = randStr(32), admin = admin).toJson().encrypt(tokenSecret)

    @BusinessSafe protected fun getUID(): Long? = getToken()?.uid//用负数代表管理员状态的uid

    protected fun getToken(): TokenWrapper? = try {
        val token = request.getHeader("token") ?: request.cookies.findLast { it.name.equals("token") }!!.value
        val decrypt = token.decrypt(tokenSecret)
        decrypt.toModel(TokenWrapper::class.java)
    } catch (e: Throwable) {
        null
    }

    fun <R> R.check(enable: Boolean = true, onCheck: (R) -> Unit): _Base {
        if (enable) onCheck.invoke(this)
        return this@_Base
    }

    /***
     * 用户权限相关的判断
     */
    fun authorized(login: Long? = null, admin: Boolean = false, strict: Boolean = false,
                   action: (SqlSession) -> Unit = { }): AuthorizedTask {
        val token = getToken()
                .require(message = "未登录用户", status = 403) { it != null }!!
                .require(message = "未授权用户", status = 403) { it.admin || login == null || it.uid == login }
        return object : _Base.AuthorizedTask {
            //对管理员权限执行检测
            override fun onCheck(session: SqlSession) {
                if (admin) {
                    session[AccountMapper::class.java].seekRankOf(token.uid).require("用户权限不足", 403) { it == 1 }
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
            val response = Maps.p("status", 200).p("message", "")
            if (result is Unit) response else response.p("data", result) //如果响应有数据,则将数据打包到响应中去
        }
        if (transaction) DBManager.dbWrite(doResponse) else DBManager.dbRead(doResponse)
    } catch(e: Throwable) {
        if (e is BusinessError) throw e
        e.printStackTrace()
        throw BusinessError("服务器错误", 502)
    }

    /***
     * 执行业务逻辑的异常的响应:
     */
    @BusinessSafe @ExceptionHandler(BusinessError::class)
    fun handleError(error: BusinessError): Map<String, Any?> = Maps.p("status", error.status).p("message", error.msg)
}


/***
 * 用以表述返回值的描述注解
 */
@MustBeDocumented @Target(AnnotationTarget.EXPRESSION) @Retention(AnnotationRetention.RUNTIME) annotation class Return

/***
 * 为 参变量提供注释
 */
@MustBeDocumented @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Doc(val value: String)

/***
 * 逻辑安全, 表征函数体本身不会主动抛出业务逻辑异常
 * 使用该类函数不用考虑异常处理
 */
@MustBeDocumented @Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BusinessSafe()

/***
 * 业务流数据流的校验方法
 */

@Throws(BusinessError::class)
fun <R> R.require(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (!filter(this)) throw BusinessError(message, status) else this
} catch(e: Throwable) {
    throw BusinessError(message, status)
}

@Throws(BusinessError::class)
fun <R> R.forbid(message: String, status: Int = 400, filter: (R) -> Boolean): R = try {
    if (filter(this)) throw BusinessError(message, status) else this
} catch (e: Throwable) {
    throw BusinessError(message, status)
}

/***
 * 主动抛出的业务逻辑错误
 */
class BusinessError(val msg: String, val status: Int) : RuntimeException(msg)
