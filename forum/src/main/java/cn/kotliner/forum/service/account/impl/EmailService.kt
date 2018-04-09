package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.utils.strDict
import cn.kotliner.forum.dao.UserRepository
import cn.kotliner.forum.service.account.api.EmailApi
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.req.CreateEmailSessionReq
import cn.kotliner.forum.service.account.resp.CreateEmailSessionResp
import cn.kotliner.forum.service.account.resp.EmailCheckTokenResp
import cn.kotliner.forum.service.article.req.EmailCheckTokenReq
import cn.kotliner.forum.service.article.req.EmailReq
import cn.kotliner.forum.domain.model.UserInfo
import org.apache.ibatis.io.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
class EmailService : EmailApi {

    @Value("\${mail.user}")
    private lateinit var emailUser: String
    @Value("\${mail.password}")
    private lateinit var emailPassword: String

    @Autowired
    private lateinit var redis: StringRedisTemplate
    @Autowired
    private lateinit var userRepo: UserRepository

    private val authenticator: Authenticator by lazy {
        object : Authenticator() { // 构建授权信息，用于进行SMTP进行身份验证
            override fun getPasswordAuthentication() =
                    PasswordAuthentication(emailUser, emailPassword)
        }
    }

    private val emailProperties: Properties by lazy {
        Resources.getResourceAsStream("application.properties").use {
            Properties().apply {
                load(it)
            }
        }
    }

    override fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp {
        val uuid = UUID.randomUUID()
        val token = "${IDs.encode(uuid.mostSignificantBits)}${IDs.encode(uuid.leastSignificantBits)}"
        redis.boundHashOps<String, Any>("email_activate:$token")
                .putAll(mapOf("uid" to "${req.uid}", "email" to req.email))
        return CreateEmailSessionResp().apply { this.token = token }
    }

    @Transactional(readOnly = false)
    override fun checkToken(req: EmailCheckTokenReq): EmailCheckTokenResp {
        val map = redis
                .boundHashOps<String, String>("email_activate:${req.token}")
                .entries()

        if (map["uid"].isNullOrBlank() || map["email"].isNullOrBlank()) abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)

        val uid = map["uid"]!!.toLong()
        val email = map["email"]!!
        val user = userRepo.getById(uid, useCache = false) ?: abort(Err.USER_NOT_EXISTS)
        if (user.email != email) abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)
        if (user.emailState == UserInfo.EmailState.TO_BE_VERIFY) {
            userRepo.update(uid, strDict { this["email_state"] = "${UserInfo.EmailState.VERIFIED}" })
        }

        return EmailCheckTokenResp().apply {
            this.uid = uid
            this.email = email
        }
    }

    override fun send(req: EmailReq) {
        Transport.send(MimeMessage(Session.getInstance(emailProperties, authenticator)).apply {
            setFrom(InternetAddress(emailUser))
            setRecipient(MimeMessage.RecipientType.TO, InternetAddress(req.to))
            subject = req.subject
            setContent(req.content, "text/html;charset=UTF-8")
        })
    }

}
