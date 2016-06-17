package tech.kotlin.china.restful.controller

import org.springframework.web.bind.annotation.*
import tech.kotlin.china.restful.model.Comment
import tech.kotlin.china.restful.model.Doc
import tech.kotlin.china.restful.model.Fail

@RestController
class CommentController : _Base() {

    @Doc("添加评论")
    @RequestMapping("/comment/make", method = arrayOf(RequestMethod.POST))
    fun makeComment(@RequestBody @Doc("评论内容") comment: Comment) = authorized {
        false
    }.session(transaction = true) {
        Fail(message = "unfinished", status = 502)
    }

    @Doc("删除评论")
    @RequestMapping("/comment/{cid}/delete", method = arrayOf(RequestMethod.POST))
    fun makeComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized {
        false
    }.session(transaction = true) {
        Fail(message = "unfinished", status = 502)
    }

}