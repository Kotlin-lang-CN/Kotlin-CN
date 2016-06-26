package tech.kotlin.china.controller.rest

import com.github.pagehelper.PageHelper
import org.springframework.web.bind.annotation.*
import tech.kotlin.china.framework.of
import tech.kotlin.china.mapper.AccountMapper
import tech.kotlin.china.mapper.MessageMapper
import tech.kotlin.china.model.Message
import tech.kotlin.china.model.MessageForm
import utils.dataflow.Doc
import utils.dataflow.forbid
import utils.dataflow.next
import utils.dataflow.require
import utils.date.format
import utils.map.expose
import utils.map.get
import utils.map.p

@RestController
class MessageController : _Rest() {

    val MESSAGE_PAGE_SIZE = 20

    @Doc("向一个用户发送一则消息, 如果管理员要发送系统消息, from属性请不填写或填null")
    @RequestMapping("/message/send")
    fun sendMessage(@RequestBody form: MessageForm) = form.check {
        it.title.forbid("请输入标题") { it.length == 0 }
        it.title.require("标题过长") { it.length < 255 }
        it.content.forbid("请输入内容") { it.length == 0 }
    }.authorized(login = form.from).session(transaction = true) {
        it.of<AccountMapper>().queryByUID(form.to).forbid("该用户不存在", 404) { it == null }
        it.of<MessageMapper>().addMessage(form expose arrayOf("content", "title", "from", "to"))
    }

    @Doc("获得我的消息数量")
    @RequestMapping("/message/count")
    fun getMyMessageCount(@RequestParam("category", defaultValue = "all") @Doc("消息状态") category: String,
                          @RequestParam("uid") @Doc("查询用户id") uid: Long) = category.check {
        it.require("未知的消息状态") { it.equals("all") || it.equals("unread") || it.equals("read") }
    }.authorized(login = uid).session {
        when (category) {
            "all" -> it.of<MessageMapper>().countAllMyMessage(uid)
            "unread" -> it.of<MessageMapper>().countMyMessage(p("uid", uid).p("status", 0))
            "read" -> it.of<MessageMapper>().countMyMessage(p("uid", uid).p("status", 1))
            else -> null
        }!!
    }

    @Doc("获得我的消息列表")
    @RequestMapping("/message/list/{page}")
    fun getMyMessageList(@RequestParam("category", defaultValue = "all") @Doc("消息状态") category: String,
                         @PathVariable("page") @Doc("分页") page: Int,
                         @RequestParam("uid") @Doc("查询用户id") uid: Long) = category.check {
        it.require("未知的消息状态") { it.equals("all") || it.equals("unread") || it.equals("read") }
    }.authorized(login = uid).session {
        PageHelper.startPage<Message>((page - 1) * MESSAGE_PAGE_SIZE + 1, page * MESSAGE_PAGE_SIZE)
        when (category) {
            "all" -> it.of<MessageMapper>().getAllMyMessage(uid)
            "unread" -> it.of<MessageMapper>().getMyMessage(p("uid", uid).p("status", 0))
            "read" -> it.of<MessageMapper>().getMyMessage(p("uid", uid).p("status", 1))
            else -> null
        }!!.map {
            it[Message::id, Message::title, Message::content]
                    .p("create_time", it.create_time.format())
                    .p("status", if (it.status == 0) "未读消息" else "已读消息")
        }
    }

    @Doc("全部标记为已读")
    @RequestMapping("/message/read/all")
    fun updateStatus() = authorized().session(transaction = true) { it.of<MessageMapper>().readAllMessages(getUID()!!) }

    @Doc("标记已读")
    @RequestMapping("/message/read/{id}")
    fun updateStatus(@PathVariable("id") id: Long) = authorized().session(transaction = true) {
        (it.of<MessageMapper>() next {
            it.queryById(id)
                    .forbid("该消息不存在") { it == null }!!
                    .forbid("没有阅读这条消息的权限", 403) { it.to != getUID() }
        }).readMessage(id)
    }
}