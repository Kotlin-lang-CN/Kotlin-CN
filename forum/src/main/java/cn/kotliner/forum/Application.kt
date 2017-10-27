package cn.kotliner.forum

import cn.kotliner.forum.service.account.api.AccountApi
import cn.kotliner.forum.utils.algorithm.Json
import cn.kotliner.forum.utils.dict
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@SpringBootApplication
@EnableTransactionManagement
class Application : WebMvcConfigurerAdapter() {

    @Bean
    fun getLogger(): Logger = LOGGER

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {
            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any?): Boolean {
                LOGGER.info(Json.dumps(dict {
                    this["url"] = "${request.requestURI} [${request.method.toUpperCase()}]"
                    this["header"] = request.headerNames.asSequence().map { it to request.getHeader(it) }.toMap()
                    this["param"] = request.parameterNames.asSequence().map { it to request.getParameter(it) }.toMap()
                }))
                return true
            }

            override fun postHandle(request: HttpServletRequest?, response: HttpServletResponse?,
                                    handler: Any?, modelAndView: ModelAndView?) = Unit

            override fun afterCompletion(request: HttpServletRequest?, response: HttpServletResponse?,
                                         handler: Any?, ex: Exception?) {
                if (ex != null) throw ex
            }
        })
    }

    companion object {
        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(Application::class.java)
    }
}

fun main(vararg args: String) {
    val context = SpringApplication.run(Application::class.java, *args)
    context.getBean(AccountApi::class.java).initAdmin()
}

