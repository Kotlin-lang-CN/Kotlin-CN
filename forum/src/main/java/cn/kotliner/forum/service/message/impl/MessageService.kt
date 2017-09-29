package cn.kotliner.forum.service.message.impl

import cn.kotliner.forum.domain.Message
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.message.api.MessageApi
import cn.kotliner.forum.service.message.req.*
import cn.kotliner.forum.service.message.resp.QueryMessageResp
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.Resource

@Service
class MessageService : MessageApi {

    @Resource private lateinit var redis: StringRedisTemplate

    override fun broadCast(req: BroadcastReq) {
        redis.boundListOps("msg:system")
                .leftPush(Json.dumps(Message().apply {
                    this.id = UUID.randomUUID().toString()
                    this.type = req.type
                    this.createTime = System.currentTimeMillis()
                    this.content = req.content
                    this.createor = req.createor
                    this.acceptor = 0
                    this.status = Message.Status.READ
                }))
    }

    override fun listCast(req: ListcastReq) {
        if (req.acceptor.isEmpty()) return

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


        redis.boundHashOps<String, String>("msg:content").putAll(msgContent)
        userMails.forEach { (acceptor, data) ->
            redis.boundListOps("msg:u:$acceptor").leftPush(data)
        }
    }

    override fun groupCast(req: GroupcastReq) {
        val msgContent = HashMap<String, String>()
        val userMails = HashMap<String, String>()

        //筛选消息接受者
        redis.boundSetOps("msg:g:${req.groupId}")
                .members()
                .filterNot { req.excludeUID.contains(it.toLong()) }
                .forEach { acceptor ->
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

        if (msgContent.isEmpty()) return

        redis.boundHashOps<String, String>("msg:content").putAll(msgContent)//创建消息

        userMails.forEach { (acceptor, data) ->
            redis.boundListOps("msg:u:$acceptor").leftPush(data) //向消息接受者邮箱中添加消息
        }
    }

    override fun getByAcceptor(req: QueryMessageReq): QueryMessageResp {
        val ids = redis.boundListOps("msg:u:${req.uid}")
                .range(req.offset.toLong(), (req.offset + req.limit).toLong())

        val msgs = if (ids.isEmpty()) ArrayList<String>() else redis
                .boundHashOps<String, String>("msg:content")
                .multiGet(ids.toList())
                .filter { it != null }

        return QueryMessageResp().apply { this.msgs = msgs.map { Json.loads<Message>(it) } }
    }

    override fun markReadReq(req: MarkReadReq) {
        val msg = redis.boundHashOps<String, String>("msg:content").get(req.id)
                ?: abort(Err.MESSAGE_NOT_EXISTS)

        val content = tryExec(Err.MESSAGE_NOT_EXISTS) { Json.loads<Message>(msg) }
        if (content.acceptor != req.acceptor) abort(Err.UNAUTHORIZED)

        redis.boundHashOps<String, String>("msg:content")
                .put(req.id, Json.dumps(content.apply { this.status = Message.Status.READ }))
    }

}