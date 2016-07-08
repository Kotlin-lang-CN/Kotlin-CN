package tech.kotlin.china.controller.rest

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tech.kotlin.china.database.ArticleMapper
import tech.kotlin.china.database.CommentMapper
import tech.kotlin.china.database.page
import tech.kotlin.china.framework._Rest
import tech.kotlin.china.framework.of
import tech.kotlin.china.model.CommentForm
import utils.dataflow.forbid
import utils.map.p
import utils.map.row

@RestController class CommentController : _Rest() {

    @RequestMapping("/comment/make", method = arrayOf(POST))
    fun makeComment(@RequestBody commentForm: CommentForm) = response {
        commentForm.content.forbid("请输入评论内容") { it.length == 0 }
        val account = auth.loginRequire()
        db write {
            it.of<ArticleMapper>().queryByAID(commentForm.aid).forbid("该文章不存在") { it == null }
            it.of<CommentMapper>().makeComment(commentForm.row().p("commenter", account.id))
        }
    }

    @RequestMapping("/comment/list", method = arrayOf(GET))
    fun commentList(@RequestParam("aid") aid: Long, @RequestParam("page", defaultValue = "1") page: Int) = response {
        db read {
            it.of<ArticleMapper>().queryByAID(aid).forbid("该文章吧存在") { it == null }
            it.page(page)
            it.of<CommentMapper>().queryByAID(aid)
        }
    }

    @RequestMapping("/comment/mine", method = arrayOf(GET))
    fun myCommentList(@RequestParam("page", defaultValue = "1") page: Int) = response {
        val account = auth.loginRequire()
        db read {
            it.page(page)
            it.of<CommentMapper>().queryMyComments(account.id)
        }
    }

    @RequestMapping("/comment/reply", method = arrayOf(GET))
    fun myReplyList(@RequestParam("page", defaultValue = "1") page: Int) = response {
        val account = auth.loginRequire()
        db read {
            it.page(page)
            it.of<CommentMapper>().queryMyReply(account.id).map {
                it.p("hint", if (it["reply"] == null) "有人评论了你的文章" else "有人@了你")
            }
        }
    }

}

