package tech.kotlin.service

import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.tryExec
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.domain.Message
import tech.kotlin.service.message.*
import tech.kotlin.service.message.req.*
import tech.kotlin.service.message.resp.QueryGroupStateResp
import tech.kotlin.service.message.resp.QueryMessageResp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object MessageService : MessageApi {

    override fun broadcast(req: BroadcastReq): EmptyResp {
        Redis {
            it.lpush("msg:system", Json.dumps(Message().apply {
                this.id = UUID.randomUUID().toString()
                this.type = req.type
                this.createTime = System.currentTimeMillis()
                this.content = req.content
                this.createor = req.createor
                this.acceptor = 0
                this.status = Message.Status.READ
            }))
        }
        return EmptyResp()
    }

    override fun listcast(req: ListcastReq): EmptyResp {
        if (req.acceptor.isEmpty()) return EmptyResp()

        Redis { redis ->
            val msgContent = HashMap<String, String>()
            val userMails = HashMap<Long, String>()
            req.acceptor.forEach { acceptor ->
                val id = UUID.randomUUID().toString()
                val data = Json.dumps(Message().apply {
                    this.id = id
                    this.type = req.type
                    this.createTime = System.nanoTime()
                    this.content = req.content
                    this.createor = req.createor
                    this.acceptor = acceptor
                    this.status = Message.Status.UNREAD
                })
                msgContent[id] = data
                userMails[acceptor] = id
            }

            redis.hmset("msg:content", msgContent)
            userMails.forEach { (acceptor, data) -> redis.lpush("msg:u:$acceptor", data) }
        }
        return EmptyResp()
    }

    override fun groupcast(req: GroupcastReq): EmptyResp {
        Redis { redis ->
            val subscriber = redis.smembers("msg:g:${req.groupId}")
            if (subscriber.isEmpty()) return@Redis

            val msgContent = HashMap<String, String>()
            val userMails = HashMap<String, String>()
            subscriber.filterNot { req.excludeUID.contains(it.toLong()) }.forEach { acceptor ->
                val id = UUID.randomUUID().toString()
                val data = Json.dumps(Message().apply {
                    this.id = id
                    this.type = req.type
                    this.createTime = System.nanoTime()
                    this.content = req.content
                    this.createor = req.createor
                    this.acceptor = acceptor.toLong()
                    this.status = Message.Status.UNREAD
                })
                msgContent[id] = data
                userMails[acceptor] = id
            }

            if (msgContent.isEmpty()) return@Redis
            redis.hmset("msg:content", msgContent)
            userMails.forEach { (acceptor, data) -> redis.lpush("msg:u:$acceptor", data) }
        }
        return EmptyResp()
    }

    override fun getByAcceptor(req: QueryMessageReq): QueryMessageResp {
        return Redis { redis ->
            val ids = redis.lrange("msg:u:${req.uid}", req.offset.toLong(), (req.offset + req.limit).toLong())
            val msgs = if (ids.isEmpty()) ArrayList<String>() else
                redis.hmget("msg:content", *ids.toTypedArray()).filter { it != null }
            return@Redis QueryMessageResp().apply { this.msgs = msgs.map { Json.loads<Message>(it) } }
        }
    }

    override fun markReadReq(req: MarkReadReq): EmptyResp {
        Redis {
            val msg = it.hget("msg:content", req.id) ?: abort(Err.MESSAGE_NOT_EXISTS)
            val content = tryExec(Err.MESSAGE_NOT_EXISTS) { Json.loads<Message>(msg) }
            if (content.acceptor != req.acceptor) abort(Err.UNAUTHORIZED)
            it.hset("msg:content", req.id, Json.dumps(content.apply { this.status = Message.Status.READ }))
        }
        return EmptyResp()
    }

}