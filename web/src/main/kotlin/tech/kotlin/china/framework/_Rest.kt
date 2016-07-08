package tech.kotlin.china.framework

import org.apache.log4j.Logger
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
import utils.dataflow.BusinessError
import utils.dataflow.BusinessSafe
import utils.map.p
import utils.properties.Env
import java.net.BindException
import java.util.*
import javax.servlet.http.HttpServletRequest

/***
 * Restful Controller
 *
 * 包含基本的请求逻辑过程函数(JWT编解码, 数据校验 check, 用户权限认证 authorized, 数据库会话 session)
 */
@RequestMapping("/api/v1")
open class _Rest {

    @Autowired lateinit protected var db: Database
    @Autowired lateinit protected var auth: JWT
    @Autowired lateinit protected var request: HttpServletRequest

    val log = Logger.getLogger("ACCESS")
    val ACCESS_LOG = Env["log"]?.contains("access") == true

    fun response(action: () -> Any?): HashMap<String, Any?> {
        try {
            if (ACCESS_LOG) log.info("""
            |--- ACCESS [LOG] -------------------------------------
            |URI: ${request.requestURI}
            |METHOD: ${request.method}
            |BODY: length = ${request.contentLength}
            """.trimMargin("|"))
            val data = action()
            return if (data == null || data is Unit)
                p("status", 200).p("message", "")
            else
                p("status", 200).p("message", "").p("data", data)
        } catch(e: Throwable) {
            if (e is BusinessError) return p("status", e.status).p("message", e.msg)
            log.info("""
            |--- DEBUG [FAIL] ------------------------------------
            """.trimMargin("|"), e)
            throw e
        }
    }

    /***
     * 执行业务逻辑的异常的响应
     */
    @BusinessSafe
    @ExceptionHandler(Throwable::class)
    open fun handleError(error: Throwable): Map<String, Any?> = when (error) {
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
        else -> p("status", 503).p("message", "未知服务器错误")
    }
}
