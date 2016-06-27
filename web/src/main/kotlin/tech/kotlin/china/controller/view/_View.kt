package tech.kotlin.china.controller.view

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import tech.kotlin.china.controller.rest._Rest
import utils.map.p
import utils.properties.Env

/***
 * ModelAndView Controller
 *
 * 使用了<a href="http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html">thymeleaf</a> 作为视图模板引擎
 * <a href="http://itutorial.thymeleaf.org/">更多更好的 thymeleaf 教程</a>
 *
 * template location: assets/templates
 * template suffix: .html
 */
@RequestMapping("/")
open class _View : _Rest() {
    val static = Env["static"] ?: "http://localhost:8080"
    fun app(fragment: String): ModelAndView = ModelAndView("app", p("static", static).p("fragment", "$fragment.app.js"))
}
