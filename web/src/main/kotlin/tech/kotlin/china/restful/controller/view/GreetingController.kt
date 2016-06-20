package tech.kotlin.china.restful.controller.view

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import tech.kotlin.china.restful.utils.Maps

@Controller
class GreetingController() : _View() {

    @RequestMapping("/greeting")
    fun greeting(@RequestParam(value = "name", required = false, defaultValue = "World") name: String)
            = view("greeting") { Maps.p("name", name) }

    @RequestMapping("/") fun index() = view("index")
}