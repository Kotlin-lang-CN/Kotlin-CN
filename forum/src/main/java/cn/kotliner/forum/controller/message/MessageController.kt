package cn.kotliner.forum.controller.message

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.Message
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.message.api.GroupApi
import cn.kotliner.forum.service.message.api.MessageApi
import cn.kotliner.forum.service.message.req.*
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import cn.kotliner.forum.exceptions.tryExec
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource


@RestController
@RequestMapping("/api/message")
class MessageController {

    @Resource
    private lateinit var req: Request
    @Resource
    private lateinit var sessionApi: SessionApi
    @Resource
    private lateinit var messageApi: MessageApi
    @Resource
    private lateinit var groupApi: GroupApi

    @GetMapping("/latest")
    fun getLatest(@RequestParam("offset", defaultValue = "0") offset: Int,
                  @RequestParam("limit", defaultValue = "20") limit: Int): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        val result = messageApi.getByAcceptor(QueryMessageReq().apply {
            this.uid = me
            this.offset = offset
            this.limit = limit
        })

        return ok {
            it["message"] = result.msgs
            it["next_offset"] = offset + result.msgs.size
        }
    }

    @PostMapping("/read/{id}")
    fun readMessage(@PathVariable("id") id: String): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        messageApi.markReadReq(MarkReadReq().apply {
            this.id = id
            this.acceptor = me
        })
        return ok()
    }

    @GetMapping("/article/subscribe/count")
    fun getSubscriberCount(@RequestParam("ids") ids: String): Resp {

        val idList = tryExec(Err.PARAMETER, "非法的id") {
            ids.split(",").filter {
                it.isNotBlank()
            }.map {
                it.trim().toLong()
            }.map {
                Message.Group.ARTICLE.format(it)
            }
        }
        return ok {
            it["count"] = groupApi.countGroup(CountGroupReq().apply {
                this.ids = idList
            }).result
        }
    }

    @GetMapping("/article/{id}/subscriber")
    fun getSubscribers(@PathVariable("id") id: Long,
                       @RequestParam("offset", defaultValue = "0") offset: Int,
                       @RequestParam("limit", defaultValue = "20") limit: Int) = ok {

        it["subscriber"] = groupApi.listGroup(ListGroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(id)
            this.offset = offset
            this.limit = limit
        }).user
    }

    @GetMapping("/article/{id}/subscribe")
    fun getSubscribeState(@PathVariable("id") id: Long): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        return ok {
            it["subscribe"] = groupApi.queryGroupState(GroupReq().apply {
                this.groupId = Message.Group.ARTICLE.format(id)
                this.uid = arrayListOf(me)
            }).result[me] ?: false
        }
    }

    @PostMapping("/article/{id}/subscribe")
    fun subscribeArticle(@PathVariable("id") id: Long): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        groupApi.joinGroup(GroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(id)
            this.uid = arrayListOf(me)
        })
        return ok()
    }

    @PostMapping("/article/{id}/unsubscribe")
    fun unSubscribeArticle(@PathVariable("id") id: Long): Resp {

        val me = sessionApi.checkToken(req.token).account.id

        groupApi.leaveGroup(GroupReq().apply {
            this.groupId = Message.Group.ARTICLE.format(id)
            this.uid = arrayListOf(me)
        })
        return ok()
    }

}
