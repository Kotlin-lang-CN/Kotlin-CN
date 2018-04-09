package cn.kotliner.forum.controller.misc

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.utils.gateway.ok
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@RestController
@RequestMapping("/api/misc")
class MiscController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var redis: StringRedisTemplate

    @GetMapping("/dashboard")
    fun getDashboard() = ok {
        it["text"] = redis.boundValueOps("dashboard").get() ?: ""
    }

    @PostMapping("/dashboard")
    fun setDashboard(@RequestParam("dashboard") dashboard: String): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        redis.boundValueOps("dashboard").set(dashboard)
        return ok()
    }

    @GetMapping("/home/link")
    fun getHomeLink() = ok {
        it["link"] = redis.boundValueOps("link").get() ?: ""
    }

    @PostMapping("/home/link")
    fun setHomeLink(@RequestParam("link") link: String): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        redis.boundValueOps("link").set(link)
        return ok()
    }

}