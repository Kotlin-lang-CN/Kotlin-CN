package tech.kotlin.china.controller.view

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/***
 * 到 React App 的路由映射
 * 后端视图模板绘制页面导航和footer以管理登录状态
 * 前段 React 绘制页面主体, 调用 restful 以完成响应
 */
@Controller
class WebController() : _View() {
    @RequestMapping("/") fun index() = app("index")
    @RequestMapping("/documentation") fun documentation() = app("documentation")
    @RequestMapping("/wiki") fun wiki() = app("wiki")
    @RequestMapping("/login") fun login() = app("login")
    @RequestMapping("/register") fun register() = app("register")
    @RequestMapping("/topic") fun topic() = app("topic")
}