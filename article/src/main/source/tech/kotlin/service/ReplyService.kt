package tech.kotlin.service

import com.github.pagehelper.PageHelper
import tech.kotlin.common.utils.IDs
import tech.kotlin.common.utils.abort
import tech.kotlin.dao.ArticleDao
import tech.kotlin.dao.ReplyDao
import tech.kotlin.service.domain.Reply
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.CreateReplyResp
import tech.kotlin.service.article.resp.ReplyListResp
import tech.kotlin.service.article.resp.QueryReplyByIdResp
import tech.kotlin.service.article.resp.QueryReplyCountByArticleResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.article.QueryReplyCountByAuthorReq
import tech.kotlin.service.article.QueryReplyCountByAuthorResp


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyService : ReplyApi {

    //创建一则文章回复
    override fun create(req: CreateArticleReplyReq): CreateReplyResp {
        val replyId = IDs.next()
        val contentId = TextService.createContent(CreateTextContentReq().apply {
            this.serializeId = "reply:$replyId"
            this.content = req.content
        }).id
        val reply = Reply().apply {
            this.id = replyId
            this.replyPoolId = "article:${req.articleId}"
            this.ownerUID = req.ownerUID
            this.createTime = System.currentTimeMillis()
            this.state = Reply.State.NORMAL
            this.contentId = contentId
            this.aliasId = req.aliasId
        }
        Mysql.write { ReplyDao.create(it, reply) }
        return CreateReplyResp().apply {
            this.replyId = reply.id
            this.contentId = contentId
        }
    }

    //改变回复状态
    override fun changeState(req: ChangeReplyStateReq): EmptyResp {
        Mysql.write {
            ReplyDao.getById(it, req.replyId, cache = false) ?: abort(Err.REPLY_NOT_EXISTS)
            ReplyDao.update(it, req.replyId, args = hashMapOf("state" to "${req.state}"))
        }
        return EmptyResp()
    }

    //通过id批量查询回复
    override fun getReplyById(req: QueryReplyByIdReq): QueryReplyByIdResp {
        val result = HashMap<Long, Reply>()
        Mysql.read { session ->
            req.id.forEach {
                val reply = ReplyDao.getById(session, it, cache = true) ?: return@forEach
                result[it] = reply
            }
        }
        return QueryReplyByIdResp().apply { this.result = result }
    }

    //查询一篇文章的所有回复
    override fun getReplyByArticle(req: QueryReplyByArticleReq): ReplyListResp {
        val result = Mysql.read {
            PageHelper.offsetPage<Reply>(req.offset, req.limit).doSelectPageInfo<Reply> {
                ReplyDao.getByPool(it, "article:${req.articleId}")
            }.list
        }
        return ReplyListResp().apply {
            this.result = result
        }
    }

    //获取文章评论数量
    override fun getReplyCountByArticle(req: QueryReplyCountByArticleReq): QueryReplyCountByArticleResp {
        val result = Mysql.read { session ->
            req.id.map { it to ReplyDao.getCountByPoolId(session, if (it == 0L) "" else "article:$it") }.toMap()
        }
        return QueryReplyCountByArticleResp().apply {
            this.result = result
        }
    }

    //获取用户创作的评论
    override fun getReplyByAuthor(req: QuerReplyByAuthorReq): ReplyListResp {
        val result = Mysql.read {
            PageHelper.offsetPage<Reply>(req.offset, req.limit).doSelectPageInfo<Reply> {
                ReplyDao.getByAuthor(it, req.author)
            }.list
        }
        return ReplyListResp().apply {
            this.result = result
        }
    }

    //获取用户评论数
    override fun getReplyCountByAuthor(req: QueryReplyCountByAuthorReq): QueryReplyCountByAuthorResp {
        val result = Mysql.read { session ->
            req.author.map { it to ReplyDao.getCountByAuthor(session, it) }.toMap()
        }
        return QueryReplyCountByAuthorResp().apply {
            this.result = result
        }
    }
}

