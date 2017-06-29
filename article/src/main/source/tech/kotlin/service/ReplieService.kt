package tech.kotlin.service

import com.github.pagehelper.PageHelper
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.utils.IDs
import tech.kotlin.common.utils.abort
import tech.kotlin.dao.ArticleDao
import tech.kotlin.dao.ReplyDao
import tech.kotlin.service.domain.Reply
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.service.article.TextApi
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.CreateReplyResp
import tech.kotlin.service.article.resp.QueryReplyByArticleResp
import tech.kotlin.service.article.resp.QueryReplyByIdResp
import tech.kotlin.service.article.resp.QueryReplyCountByArticleResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.common.mysql.Mysql


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplieService : ReplyApi {

    val textApi by Serv.bind(TextApi::class)

    //创建一则文章回复
    override fun create(req: CreateArticleReplyReq): CreateReplyResp {
        val replyId = IDs.next()
        val contentId = textApi.createContent(CreateTextContentReq().apply {
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
        Mysql.write {
            if (reply.aliasId != 0L) {
                val alias = ReplyDao.getById(it, req.aliasId, cache = false) ?:
                        abort(Err.REPLY_NOT_EXISTS, "关联评论不存在")

                if (alias.replyPoolId != "article:${req.articleId}")
                    abort(Err.REPLY_NOT_EXISTS, "关联评论不存在")
            }
            ArticleDao.getById(it, req.articleId, false) ?: abort(Err.ARTICLE_NOT_EXISTS)
            ReplyDao.create(it, reply)
        }
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
    override fun getReplyByArticle(req: QueryReplyByArticleReq): QueryReplyByArticleResp {
        val result = Mysql.read {
            PageHelper.offsetPage<Reply>(req.offset, req.limit
            ).doSelectPageInfo<Reply> {
                ReplyDao.getByPool(it, "article:${req.articleId}")
            }.list
        }
        return QueryReplyByArticleResp().apply {
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
}

