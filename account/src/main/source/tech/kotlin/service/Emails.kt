package tech.kotlin.service

import tech.kotlin.model.request.EmailReq
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.Looper
import tech.kotlin.common.utils.Props
import tech.kotlin.common.utils.str
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
object Emails {

    val handler = Handler(Looper.getMainLooper())
    val properties = Props.loads("project.properties")
    val authenticator: Authenticator by lazy {
        // 构建授权信息，用于进行SMTP进行身份验证
        object : Authenticator() {
            override fun getPasswordAuthentication()=
                    PasswordAuthentication(properties str "mail.user", properties str "mail.password")
        }
    }

    fun send(req: EmailReq): EmptyResp {
        val task = Runnable {
            Transport.send(MimeMessage(Session.getInstance(properties, authenticator)).apply {
                setFrom(InternetAddress(properties str "mail.user"))
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
