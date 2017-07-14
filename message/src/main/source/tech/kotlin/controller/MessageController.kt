package tech.kotlin.controller

import spark.Route
import sun.misc.resources.Messages
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.ok
import tech.kotlin.common.utils.tryExec
import tech.kotlin.service.Err
import tech.kotlin.service.GroupService
import tech.kotlin.service.MessageService
import tech.kotlin.service.ServDef
import tech.kotlin.service.account.SessionApi
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.domain.Message
import tech.kotlin.service.message.CountGroupReq
import tech.kotlin.service.message.req.GroupReq
import tech.kotlin.service.message.req.ListGroupReq
import tech.kotlin.service.message.req.MarkReadReq
import tech.kotlin.service.message.req.QueryMessageReq

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MessageController {

    val sessionApi by Serv.bind(SessionApi::class, ServDef.ACCOUNT)

    //个人未读消息数
    val unread = Route { req, _ ->
        val offset = req.queryParams("offset")
                             ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                             ?.toInt()
                     ?: 0

        val limit = req.queryParams("limit")
                            ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                            ?.toInt()
                    ?: 20

        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id
        val result = MessageService.getByAcceptor(QueryMessageReq().apply {
            this.uid = me
            this.offset = offset
            this.limit = limit
        })

        return@Route ok {
            it["message"] = result.msgs
        }
    }

    //标记已读
    val markRead = Route { req, _ ->
        val id = req.params(":id") ?: abort(Err.PARAMETER, "empty id")
        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id
        MessageService.markReadReq(MarkReadReq().apply {
            this.id = id
            this.acceptor = me
        })
        return@Route ok()
    }

    //订阅文章评论
    val subscribeArticle = Route { req, _ ->
        val articleId = req.params(":id")?.tryExec(Err.PARAMETER) { it.toLong() }
                        ?: abort(Err.PARAMETER, "empty article id")
        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id
        GroupService.joinGroup(GroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(articleId)
            this.uid = arrayListOf(me)
        })
        return@Route ok()
    }

    //取消订阅文章
    val unsubscribeArticle = Route { req, _ ->
        val articleId = req.params(":id")?.tryExec(Err.PARAMETER) { it.toLong() }
                        ?: abort(Err.PARAMETER, "empty article id")
        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id
        GroupService.leaveGroup(GroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(articleId)
            this.uid = arrayListOf(me)
        })
        return@Route ok()
    }

    //查询文章订阅情况
    val subscribeState = Route { req, _ ->
        val articleId = req.params(":id")?.tryExec(Err.PARAMETER) { it.toLong() }
                        ?: abort(Err.PARAMETER, "empty article id")
        val me = sessionApi.checkToken(CheckTokenReq(req)).account.id
        val state = GroupService.queryGroupState(GroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(articleId)
            this.uid = arrayListOf(me)
        }).result[me] ?: false
        return@Route ok { it["subscribe"] = state }
    }

    //查询文章订阅者
    val articleSubscriber = Route { req, _ ->
        val articleId = req.params(":id")?.tryExec(Err.PARAMETER) { it.toLong() }
                        ?: abort(Err.PARAMETER, "empty article id")
        val offset = req.queryParams("offset")
                             ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                             ?.toInt()
                     ?: 0
        val limit = req.queryParams("limit")
                            ?.apply { check(Err.PARAMETER) { it.toInt();true } }
                            ?.toInt()
                    ?: 20
        return@Route ok {
            it["subscriber"] = GroupService.listGroup(ListGroupReq().apply {
                this.groupId = Message.Group.ARTICLE.format(articleId)
                this.offset = offset
                this.limit = limit
            }).user
        }
    }

    //文章订阅者数
    val articleSubscriberCount = Route { req, _ ->
        val ids = req.tryExec(Err.PARAMETER, "非法的id") {
            it.queryParams("ids").split(",").filter { it.isNotBlank() }.map { it.trim().toLong() }.map { "reply:$it" }
        }
        return@Route ok {
            it["count"] = GroupService.countGroup(CountGroupReq().apply { this.ids = ids }).result
        }
    }

}