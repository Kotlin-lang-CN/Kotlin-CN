package cn.kotliner.forum.controller.article

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.model.Account
import cn.kotliner.forum.domain.model.Reply
import cn.kotliner.forum.domain.model.TextContent
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.article.api.ReplyApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.utils.*
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank
import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/reply")
class ReplyController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var replyApi: ReplyApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var textApi: TextApi

    //批量查询文章回复数
    @GetMapping("/count")
    fun getReplyCount(@RequestParam("id", defaultValue = "0") id: String): Resp {
        val queryId = id
                .check(Err.PARAMETER) { it.split(',').map { it.toLong() };true }
                .split(',')
                .map { it.toLong() }

        return ok {
            it["data"] = replyApi.getReplyCountByArticle(
                    QueryReplyCountByArticleReq().apply { this.id = queryId }
            ).result
        }
    }

    //获取文章回复列表
    @GetMapping("/article/{id}")
    fun getArticleReply(
            @PathVariable("id") articleId: Long,
            @RequestParam("offset", defaultValue = "0") offset: Int,
            @Range(min = 0, max = 20)
            @RequestParam("limit", defaultValue = "20") limit: Int
    ): Resp {
        val reply = replyApi.getReplyByArticle(QueryReplyByArticleReq().apply {
            this.articleId = articleId
            this.offset = offset
            this.limit = limit
        }).result

        val users = HashMap<Long, UserInfo>()
        val contents = HashMap<Long, TextContent>()
        if (reply.isNotEmpty()) {
            users.putAll(userApi.queryById(QueryUserReq().apply {
                this.id = ArrayList<Long>().apply {
                    addAll(reply.map { it.ownerUID })
                    addAll(reply.map { it.aliasId }.filter { it != 0L })
                }
            }).info)
            contents.putAll(textApi.getById(QueryTextReq().apply {
                this.id = reply.map { it.contentId }.toList()
            }).result)
        }

        //只有管理员才能看到封禁和删除的文章内容
        var isUserAdmin = false
        try {
            val account = sessionApi.checkToken(req.token).account
            isUserAdmin = account.role == Account.Role.ADMIN
        } catch (ignore: Throwable) {
        }

        return ok {
            it["reply"] = reply.map {
                dict {
                    this["meta"] = it
                    this["user"] = users[it.ownerUID] ?: UserInfo()
                    this["content"] =
                            if (isUserAdmin || it.state == Reply.State.NORMAL)
                                contents[it.contentId] ?: TextContent()
                            else
                                TextContent()
                    users[it.aliasId]?.apply { this@dict += "alias" to this }
                }
            }
            it["next_offset"] = offset + reply.size
        }
    }

    //参与文章回复
    @PostMapping("/article/{id}")
    fun createArticleReply(
            @PathVariable("id") articleId: Long,
            @NotBlank(message = "评论内容为空")
            @Length(min = 10, message = "评论字数必须超过10个字")
            @RequestParam("content") content: String,
            @RequestParam("alias_id", defaultValue = "0") aliasId: Long
    ): Resp {
        val owner = sessionApi.checkToken(req.token)

        val createResp = replyApi.create(CreateArticleReplyReq().apply {
            this.articleId = articleId
            this.ownerUID = owner.account.id
            this.content = content
            this.aliasId = aliasId
        })
        return ok { it["id"] = createResp.replyId }
    }


    //删除文章评论
    @PostMapping("/id/{id}/delete")
    fun deleteReply(@PathVariable("id") replyId: Long): Resp {
        val owner = sessionApi.checkToken(req.token).account

        val reply: Reply = replyApi.getReplyById(QueryReplyByIdReq().apply {
            this.id = arrayListOf(replyId)
        }).result[replyId] ?: abort(Err.REPLY_NOT_EXISTS)

        //只有评论作者和管理员能删除评论
        if (reply.ownerUID == owner.id || owner.role == Account.Role.ADMIN) {
            replyApi.changeState(ChangeReplyStateReq().apply {
                this.replyId = replyId
                this.state = Reply.State.DELETE
            })
        } else {
            abort(Err.UNAUTHORIZED)
        }

        return ok()
    }

}
