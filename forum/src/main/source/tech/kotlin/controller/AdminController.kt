package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Reply
import tech.kotlin.model.request.ChangeReplyStateReq
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.article.ReplyService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object AdminController {

    val userState = Route { req, _ -> todo() }

    val userSetting = Route { req, _ -> todo() }

    val articleSetting = Route { req, _ -> todo() }

    val replySetting = Route { req, _ ->
        val replyId = req.params(":id").check(Err.PARAMETER) { it.toLong();true }.toLong()
        val state = req.queryParams("state").check(Err.PARAMETER) { s ->
            arrayOf(Reply.State.NORMAL, Reply.State.BAN, Reply.State.DELETE).any { it == s.toInt() }
        }.toInt()

        val owner = TokenService.checkToken(CheckTokenReq(req)).account
        owner.check(Err.UNAUTHORIZED) { it.role == Account.Role.ADMIN }

        ReplyService.changeState(ChangeReplyStateReq().apply {
            this.replyId = replyId
            this.state = state
        })

        return@Route ok()
    }
}