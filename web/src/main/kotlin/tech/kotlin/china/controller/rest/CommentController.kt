package tech.kotlin.china.controller.rest

import com.github.pagehelper.PageHelper
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.POST
import tech.kotlin.china.database.*
import tech.kotlin.china.model.Comment
import tech.kotlin.china.model.CommentForm
import tech.kotlin.china.utils.*

@RestController
class CommentController : _Rest() {

    val COMMENT_PAGE_SIZE = 20 //账号列表的分页大小

    @Doc("文章评论列表")
    @RequestMapping("/comment/list/{page}")
    fun commentList(@PathVariable("page") @Doc("分页") page: Int, @RequestParam("aid") @Doc("文章id") aid: Long,
                    @RequestParam("category", defaultValue = "create_time") @Doc("排序规则") category: String) = session {
        it.map(CommentMapper::class) {
            PageHelper.startPage<Comment>((page - 1) * COMMENT_PAGE_SIZE + 1, page * COMMENT_PAGE_SIZE)
            @Return it.queryByAID(aid).map {
                it[Comment::cid, Comment::commenter, Comment::reply, Comment::flower, Comment::content]
                        .p("create_time", it.create_time.format())
            }
        }
    }

    @Doc("查看我的评论列表")
    @RequestMapping("/comment/mine/{page}")
    fun myCommentList(@RequestParam("category", defaultValue = "time") @Doc("排序规则") category: String,
                      @PathVariable("page") @Doc("分页") page: Int) = authorized().session {
        it.map(CommentMapper::class) {
            PageHelper.startPage<Comment>((page - 1) * COMMENT_PAGE_SIZE + 1, page * COMMENT_PAGE_SIZE)
            @Return it.queryByUID(getUID()!!).map {
                it[Comment::cid, Comment::aid, Comment::commenter, Comment::reply, Comment::flower, Comment::content]
                        .p("create_time", it.create_time.format())
                        .p("status", if (it.forbidden) "管理员锁定" else "正常")
            }
        }
    }

    @Doc("参与评论")
    @RequestMapping("/comment/make", method = arrayOf(POST))
    fun makeComment(@RequestBody @Doc("评论内容") form: CommentForm) = form.check {
        it.content.require("请填写评论内容") { it.length != 0 }
    }.authorized(strict = true) {
        form.reply.forbid("不能回复自己的评论") { it == getUID()!! }
        it.map<ArticleMapper>().queryByAID(form.aid)
                .forbid("该文章不存在", 404) { it == null }!!
        if (form.reply != null) it.map<AccountMapper>().queryByUID(form.reply)
                .forbid("不存在该用户", 404) { it == null }
    }.session(transaction = true) {
        it.map<CommentMapper>().makeComment(
                form[CommentForm::content, CommentForm::aid, CommentForm::reply].p("uid", getUID()!!)
        )
        val count = it.map<CommentMapper>().countComment(form.aid)
        it.map<ArticleMapper>().updateCommentCount(p("aid", form.aid).p("comment", count))
    }

    @Doc("删除评论")
    @RequestMapping("/comment/{cid}/delete", method = arrayOf(POST))
    fun makeComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized {
        it.map<CommentMapper>().queryByCID(cid)
                .forbid("该评论根本不存在!", 404) { it == null }!!
                .require("您没有删除该评论的权利") { it.commenter == getUID()!! }
    }.session(transaction = true) {
        val comment = it.map<CommentMapper>().queryByCID(cid).forbid("该评论不存在", 404) { it == null }!!
        it.map<CommentMapper>().deleteComment(cid)
        val count = it.map<CommentMapper>().countComment(comment.aid)
        it.map<ArticleMapper>().updateCommentCount(p("aid", comment.aid).p("comment", count))
    }

    @Doc("点赞评论")
    @RequestMapping("/comment/{cid}/flower", method = arrayOf(POST))
    fun praiseComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized(strict = true) {
        it.map<FlowerMapper>().queryActor(p("mode", 1).p("oid", cid).p("actor", getUID()!!))
                .forbid("你已经点过赞了!") { it != 0 }
    }.session(transaction = true) {
        val args = p("mode", 1).p("oid", cid).p("actor", getUID()!!)
        it.map<FlowerMapper>().addFlower(args)
        val count = it.map<FlowerMapper>().objectPraisedCount(args)
        it.map<CommentMapper>().updateFlowerCount(p("flower", count).p("cid", cid))
    }

    @Doc("取消评论点赞")
    @RequestMapping("/comment/{cid}/flower/cancel", method = arrayOf(POST))
    fun cancelPraiseComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized(strict = true) {
        it.map<FlowerMapper>().queryActor(p("mode", 1).p("oid", cid).p("actor", getUID()!!))
                .forbid("你还没点过赞呢!") { it == 0 }
    }.session(transaction = true) {
        val args = p("mode", 1).p("oid", cid).p("actor", getUID()!!)
        it.map<FlowerMapper>().cancelFlower(args)
        val count = it.map<FlowerMapper>().objectPraisedCount(args)
        it.map<CommentMapper>().updateFlowerCount(p("flower", count).p("cid", cid))
    }

    @Doc("封禁评论")
    @RequestMapping("/comment/{cid}/disable", method = arrayOf(POST))
    fun disableComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized(admin = true)
            .session(transaction = true) {
                val commentMapper = it.map<CommentMapper>()
                val articleMapper = it.map<ArticleMapper>()

                val comment = commentMapper.queryByCID(cid).forbid("该评论不存在", 404) { it == null }!!
                commentMapper.enableComment(p("disable", true).p("cid", cid))
                val count = commentMapper.countComment(comment.aid)
                articleMapper.updateCommentCount(p("aid", comment.aid).p("comment", count))
            }

    @Doc("解封评论")
    @RequestMapping("/comment/{cid}/enable", method = arrayOf(POST))
    fun enableComment(@PathVariable("cid") @Doc("评论id") cid: Long) = authorized(admin = true)
            .session(transaction = true) {
                val commentMapper = it.map<CommentMapper>()
                val articleMapper = it.map<ArticleMapper>()

                val comment = commentMapper.queryByCID(cid).forbid("该评论不存在", 404) { it == null }!!
                commentMapper.enableComment(p("disable", false).p("cid", cid))
                val count = commentMapper.countComment(comment.aid)
                articleMapper.updateCommentCount(p("aid", comment.aid).p("comment", count))
            }
}
