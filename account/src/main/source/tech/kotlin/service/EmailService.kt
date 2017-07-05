package tech.kotlin.service

import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.Looper
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.utils.*
import tech.kotlin.service.account.req.CreateEmailSessionReq
import tech.kotlin.service.article.req.EmailCheckTokenReq
import tech.kotlin.service.article.req.EmailReq
import tech.kotlin.service.account.resp.CreateEmailSessionResp
import tech.kotlin.service.account.resp.EmailCheckTokenResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.account.EmailApi
import java.util.*
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object EmailService : EmailApi {

    val handler = Handler(Looper.getMainLooper())
    val authenticator: Authenticator by lazy {
        object : Authenticator() { // 构建授权信息，用于进行SMTP进行身份验证
            override fun getPasswordAuthentication() =
                    PasswordAuthentication(Props str "mail.user",
                                           Props str "mail.password")
        }
    }

    override fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp {
        val uuid = UUID.randomUUID()
        val token = "${IDs.encode(uuid.mostSignificantBits)}${IDs.encode(uuid.leastSignificantBits)}"
        Redis {
            it.hmset("email_activate:$token", mapOf("uid" to "${req.uid}", "email" to req.email))
        }
        return CreateEmailSessionResp().apply { this.token = token }
    }

    override fun checkToken(req: EmailCheckTokenReq): EmailCheckTokenResp {
        val map = Redis { it.hgetAll("email_activate:${req.token}") }
        if (map["uid"].isNullOrBlank() || map["email"].isNullOrBlank())
            abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)
        val uid = map["uid"]!!.toLong()
        val email = map["email"]!!
//        Mysql.write {
//            val user = UserInfoDao.getById(it, uid) ?: abort(Err.USER_NOT_EXISTS)
//            if (user.email != email) abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)
//            if (user.emailState == UserInfo.EmailState.TO_BE_VERIFY) {
//                UserInfoDao.update(it, uid, strDict {
//                    this["email_state"] = "${UserInfo.EmailState.VERIFIED}"
//                })
//            }
//        }
        return EmailCheckTokenResp().apply {
            this.uid = uid
            this.email = email
        }
    }

    override fun send(req: EmailReq): EmptyResp {
        val task = Runnable {
            Transport.send(MimeMessage(Session.getInstance(Props, authenticator)).apply {
                setFrom(InternetAddress(Props str "mail.user"))
                setRecipient(MimeMessage.RecipientType.TO, InternetAddress(req.to))
                subject = req.subject
                setContent(req.content, "text/html;charset=UTF-8")
            })
        }
        if (req.async) {
            handler.post(task)
        } else {
            task.run()
        }
        return EmptyResp()
    }

}
