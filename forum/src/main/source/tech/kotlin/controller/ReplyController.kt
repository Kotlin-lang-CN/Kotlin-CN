package tech.kotlin.controller

import spark.Route
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Reply
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.*
import tech.kotlin.service.account.TokenService
import tech.kotlin.service.account.UserService
import tech.kotlin.model.request.QueryReplyByArticleReq
import tech.kotlin.service.article.ReplyService
import tech.kotlin.service.article.TextService
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyController {

    val createReply = Route { req, _ ->
        val articleId = req.params(":id").check(Err.PARAMETER) { it.toLong(); true }.toLong()
        val content = req.queryParams("content").check(Err.PARAMETER, "评论内容为空") { !it.isNullOrBlank() }
        val aliasId = req.queryParams("alias_id")?.check(Err.PARAMETER, "非法的关联id") { it.toLong();true }?.toLong() ?: 0

        val owner = TokenService.checkToken(CheckTokenReq(req)).account

        val createResp = ReplyService.create(CreateArticleReplyReq().apply {
            this.articleId = articleId
            this.ownerUID = owner.id
            this.content = content
            this.aliasId = aliasId
        })

        return@Route ok { it["id"] = createResp.replyId }
    }

    val delReply = Route { req, _ ->
        val replyId = req.params(":reply_id").check(Err.PARAMETER) { it.toLong();true }.toLong()

        val owner = TokenService.checkToken(CheckTokenReq(req)).account
        val reply = ReplyService.getReplyById(QueryReplyByIdReq().apply {
            this.id = arrayListOf(replyId)
        }).result[replyId] ?: abort(Err.REPLY_NOT_EXISTS)

        if (reply.ownerUID == owner.id || owner.role == Account.Role.ADMIN) {
            ReplyService.changeState(ChangeReplyStateReq().apply {
                this.replyId = replyId
                this.state = Reply.State.DELETE
            })
        }
        return@Route ok()
    }

    val control = Route { req, _ ->
        val replyId = req.params(":reply_id").check(Err.PARAMETER) { it.toLong();true }.toLong()
        val state = req.queryParams("state").check(Err.PARAMETER) { s ->
            arrayOf(Reply.State.NORMAL, Reply.State.BAN, Reply.State.DELETE).any { it == s.toInt() }
        }.toInt()

        val owner = TokenService.checkToken(CheckTokenReq(req)).account

        if (owner.role == Account.Role.ADMIN) {
            ReplyService.changeState(ChangeReplyStateReq().apply {
                this.replyId = replyId
                this.state = state
            })
        }
        return@Route ok()
    }

    val queryReply = Route { req, _ ->
        val articleId = req.params(":id").check(Err.PARAMETER) { it.toLong();true }.toLong()
        val offset = req.queryParams("offset")?.apply { check(Err.PARAMETER) { it.toInt();true } }?.toInt() ?: 0
        val limit = req.queryParams("limit")?.apply { check(Err.PARAMETER) { it.toInt();true } }?.toInt() ?: 20

        val reply = ReplyService.getReplyByArticle(QueryReplyByArticleReq().apply {
            this.articleId = articleId
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        val contents = HashMap<Long, TextContent>()
        if (reply.isNotEmpty()) {
            users.putAll(UserService.queryById(QueryUserReq().apply {
                this.id = reply.map { it.ownerUID }.toList()
            }).info)
            contents.putAll(TextService.getById(QueryTextReq().apply {
                this.id = reply.map { it.contentId }.toList()
            }).result)
        }

        return@Route ok {
            it["reply"] = reply.map {
                hashMapOf(
                        "meta" to it,
                        "content" to (contents[it.contentId] ?: TextContent()),
                        "user" to (users[it.ownerUID] ?: UserInfo())
                )
            }
            it["next_offset"] = offset + reply.size
        }
    }

}