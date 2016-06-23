package tech.kotlin.china.controller.view

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import tech.kotlin.china.controller.rest._Rest
import tech.kotlin.china.utils.Env
import tech.kotlin.china.utils.p
import java.util.*

/***
 * ModelAndView Controller
 *
 * 使用了<a href="http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html">thymeleaf</a> 作为视图模板引擎
 * <a href="http://itutorial.thymeleaf.org/">更多更好的 thymeleaf 教程</a>
 * template location: resources/templates
 * template suffix: .html
 */
@RequestMapping("/view")
open class _View : _Rest() {

    val static = Env["static"] ?: "http://localhost:8080"

    /***
     *  为 view 中填充元数据
     */
    fun view(view: String, model: () -> HashMap<String, Any?> = { HashMap() }): ModelAndView {
        val token = getToken()
        return ModelAndView(view, model.invoke()
                .p("static", static)
                .p("meta", p("login", token != null)
                        .p("uid", token?.uid)
                        .p("username", token?.username)
                        .p("admin", token != null && token.admin)
                ))
    }
}
