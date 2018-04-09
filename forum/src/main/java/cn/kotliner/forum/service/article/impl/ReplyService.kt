package cn.kotliner.forum.service.article.impl

import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.dao.ReplyRepository
import cn.kotliner.forum.domain.model.Article
import cn.kotliner.forum.domain.model.Message
import cn.kotliner.forum.domain.model.Reply
import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.ReplyApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.message.api.MessageApi
import cn.kotliner.forum.service.message.req.GroupcastReq
import cn.kotliner.forum.service.message.req.ListcastReq
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.service.article.req.QueryReplyCountByAuthorReq
import cn.kotliner.forum.service.article.resp.QueryReplyCountByAuthorResp
import cn.kotliner.forum.service.article.resp.CreateReplyResp
import cn.kotliner.forum.service.article.resp.QueryReplyByIdResp
import cn.kotliner.forum.service.article.resp.QueryReplyCountByArticleResp
import cn.kotliner.forum.service.article.resp.ReplyListResp
import cn.kotliner.forum.utils.algorithm.Json
import cn.kotliner.forum.utils.dict
import com.github.pagehelper.PageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
@Service
class ReplyService : ReplyApi {

    @Resource private lateinit var messageApi: MessageApi
    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var textApi: TextApi
    @Resource private lateinit var replyRepo: ReplyRepository

    //创建一则文章回复
    @Transactional(readOnly = false)
    override fun create(req: CreateArticleReplyReq): CreateReplyResp {
        val replyId = IDs.next()

        val contentId = textApi.createContent(CreateTextContentReq().apply {
            this.serializeId = "reply:$replyId"
            this.content = req.content
        }).id

        val reply = Reply().apply {
            this.id = replyId
            this.replyPoolId = "${Reply.Pool.ARTICLE}:${req.articleId}"
            this.ownerUID = req.ownerUID
            this.createTime = System.currentTimeMillis()
            this.state = Reply.State.NORMAL
            this.contentId = contentId
            this.aliasId = req.aliasId
        }
        replyRepo.create(reply)

        //send message to describer
        sendMsg(req, reply)
        return CreateReplyResp().apply {
            this.replyId = reply.id
            this.contentId = contentId
        }
    }

    //改变回复状态
    @Transactional(readOnly = false)
    override fun changeState(req: ChangeReplyStateReq) {
        replyRepo.getById(req.replyId, useCache = false) ?: abort(Err.REPLY_NOT_EXISTS)
        replyRepo.update(req.replyId, args = hashMapOf("state" to "${req.state}"))
    }

    //通过id批量查询回复
    @Transactional(readOnly = true)
    override fun getReplyById(req: QueryReplyByIdReq): QueryReplyByIdResp {
        val result = HashMap<Long, Reply>()

        req.id.forEach {
            val reply = replyRepo.getById(it, useCache = true) ?: return@forEach
            result[it] = reply
        }

        return QueryReplyByIdResp().apply { this.result = result }
    }

    //查询一篇文章的所有回复
    @Transactional(readOnly = true)
    override fun getReplyByArticle(req: QueryReplyByArticleReq): ReplyListResp {
        val result = PageHelper.offsetPage<Reply>(req.offset, req.limit).doSelectPageInfo<Reply> {
            replyRepo.getByPool("${Reply.Pool.ARTICLE}:${req.articleId}")
        }.list

        return ReplyListResp().apply {
            this.result = result
        }
    }

    //获取文章评论数量
    @Transactional(readOnly = true)
    override fun getReplyCountByArticle(req: QueryReplyCountByArticleReq): QueryReplyCountByArticleResp {
        val result = req.id.map {
            it to replyRepo.getCountByPoolId(if (it == 0L) "" else "${Reply.Pool.ARTICLE}:$it")
        }.toMap()

        return QueryReplyCountByArticleResp().apply {
            this.result = result
        }
    }

    //获取用户创作的评论
    @Transactional(readOnly = true)
    override fun getReplyByAuthor(req: QuerReplyByAuthorReq): ReplyListResp {
        val result = PageHelper
                .offsetPage<Reply>(req.offset, req.limit)
                .doSelectPageInfo<Reply> {
                    replyRepo.getByAuthor(req.author)
                }.list

        return ReplyListResp().apply {
            this.result = result
        }
    }

    //获取用户评论数
    @Transactional(readOnly = true)
    override fun getReplyCountByAuthor(req: QueryReplyCountByAuthorReq): QueryReplyCountByAuthorResp {
        val result = req.author.map {
            it to replyRepo.getCountByAuthor(it)
        }.toMap()

        return QueryReplyCountByAuthorResp().apply {
            this.result = result
        }
    }

    private fun sendMsg(req: CreateArticleReplyReq, reply: Reply) {
        val article: Article = articleApi.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(req.articleId)
        }).articles[req.articleId] ?: return

        val from = userApi.queryById(QueryUserReq().apply {
            this.id = arrayListOf(req.ownerUID)
        }).info[req.ownerUID] ?: UserInfo()

        //关注文章通知
        messageApi.groupCast(GroupcastReq().apply {
            this.type = Message.Type.ToArticle
            this.content = Json.dumps(dict {
                this["from"] = from
                this["article"] = article
                this["reply"] = reply
                this["content"] = req.content
            })
            this.createor = req.ownerUID
            this.groupId = Message.Group.ARTICLE.format(req.articleId)
            this.excludeUID = ArrayList<Long>().apply {
                if (article.author != req.ownerUID) add(req.ownerUID)
                if (req.aliasId != 0L) add(req.aliasId)
            }
        })
        //对用户评论通知
        if (req.aliasId == 0L) return
        messageApi.listCast(ListcastReq().apply {
            this.type = Message.Type.ToReply
            this.content = Json.dumps(dict {
                this["from"] = from
                this["article"] = article
                this["reply"] = reply
                this["content"] = req.content
            })
            this.createor = req.ownerUID
            this.acceptor = arrayListOf(req.aliasId)
        })
    }
}

