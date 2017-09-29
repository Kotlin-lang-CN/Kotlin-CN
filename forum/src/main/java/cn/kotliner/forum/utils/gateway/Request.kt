package cn.kotliner.forum.utils.gateway

import cn.kotliner.forum.domain.Device
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.req.CheckTokenReq
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

@Component
class Request {

    @Suppress("SpringKotlinAutowiring")
    @Lazy
    @Resource
    private lateinit var req: HttpServletRequest

    val device: Device
        get() = Device().apply {
            token = (req.getHeader("X-App-Device") ?: "")
                    .check(Err.TOKEN_FAIL, "缺失设备信息") { !it.isBlank() }

            platform = req.getHeader("X-App-Platform")?.apply {
                check(Err.TOKEN_FAIL, "缺失设备信息") { it.toInt();true }
            }?.toInt() ?: 0

            vendor = (req.getHeader("X-App-Vendor") ?: "")
                    .check(Err.TOKEN_FAIL, "缺失设备信息") { !it.isBlank() }

            system = (req.getHeader("X-App-System") ?: "")
                    .check(Err.TOKEN_FAIL, "缺失设备信息") { !it.isBlank() }
        }

    val token: CheckTokenReq
        get() = CheckTokenReq().apply {
            this.device = this@Request.device
            this.token = req.getHeader("X-App-Token")
                    ?: req.cookies.find { it.name == "X-App-Token" }?.value
                    ?: abort(Err.TOKEN_FAIL, "缺失登录信息")
        }

}