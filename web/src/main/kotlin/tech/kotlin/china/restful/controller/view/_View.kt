package tech.kotlin.china.restful.controller.view

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import tech.kotlin.china.restful.controller.rest._Rest
import tech.kotlin.china.restful.utils.Maps
import tech.kotlin.china.restful.utils.p
import java.util.*

/***
 * 所有 ModelAndView Controller 的基类
 *
 * 使用了<a href="http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html">thymeleaf</a> 作为视图模板引擎
 * template location: resources/templates
 * template suffix: .html
 */
@RequestMapping("/")
open class _View : _Rest() {

    /***
     *  为 view 中填充元数据
     */
    fun view(view: String, model: () -> HashMap<String, Any?> = { HashMap() }): ModelAndView {
        val token = getToken()
        return ModelAndView(view, model.invoke().p("meta", Maps
                .p("login", token != null)
                .p("uid", token?.uid)
                .p("username", token?.uid)
                .p("admin", token != null && token.admin)
        ))
    }
}
