package tech.kotlin.china.controller.rest

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.kotlin.china.database.AccountMapper
import tech.kotlin.china.database.MessageMapper
import tech.kotlin.china.database.map
import tech.kotlin.china.model.MessageForm
import tech.kotlin.china.utils.Doc
import tech.kotlin.china.utils.forbid
import tech.kotlin.china.utils.require

@RestController
class MessageController : _Rest() {

    @Doc("向一个用户发送一则消息")
    @RequestMapping("/send/message")
    fun sendMessage(@RequestBody form: MessageForm) = form.check {
        it.title.require("请输入标题") { it.length == 0 }
        it.title.require("标题过长") { it.length < 255 }
        it.content.require("请输入内容") { it.length == 0 }
    }.authorized(login = form.from, strict = true) {
        it.map<AccountMapper>()
                .queryByUID(form.to).forbid("该用户不存在", 404) { it == null }
    }.session(transaction = true) {
        it.map<MessageMapper>()
    }
}