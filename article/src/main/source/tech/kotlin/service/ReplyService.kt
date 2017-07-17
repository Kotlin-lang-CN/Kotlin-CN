package tech.kotlin.service

import com.github.pagehelper.PageHelper
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.IDs
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.dict
import tech.kotlin.dao.ReplyDao
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.article.QueryReplyCountByAuthorReq
import tech.kotlin.service.article.QueryReplyCountByAuthorResp
import tech.kotlin.service.article.ReplyApi
import tech.kotlin.service.article.req.*
import tech.kotlin.service.article.resp.CreateReplyResp
import tech.kotlin.service.article.resp.QueryReplyByIdResp
import tech.kotlin.service.article.resp.QueryReplyCountByArticleResp
import tech.kotlin.service.article.resp.ReplyListResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.domain.Message
import tech.kotlin.service.domain.Reply
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.message.req.GroupcastReq
import tech.kotlin.service.message.MessageApi
import tech.kotlin.service.message.req.ListcastReq


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyService : ReplyApi {

    val messageApi by Serv.bind(MessageApi::class, ServDef.MESSAGE)
    val userApi by Serv.bind(UserApi::class, ServDef.ACCOUNT)

    //创建一则文章回复
    override fun create(req: CreateArticleReplyReq): CreateReplyResp {
        val replyId = IDs.next()
        val contentId = TextService.createContent(CreateTextContentReq().apply {
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
        Mysql.write { ReplyDao.create(it, reply) }

        //send message to describer
        sendMsg(req)
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
                ReplyDao.getByPool(it, "${Reply.Pool.ARTICLE}:${req.articleId}")
            }.list
        }
        return ReplyListResp().apply {
            this.result = result
        }
    }

    //获取文章评论数量
    override fun getReplyCountByArticle(req: QueryReplyCountByArticleReq): QueryReplyCountByArticleResp {
        val result = Mysql.read { session ->
            req.id.map {
                it to ReplyDao.getCountByPoolId(session, if (it == 0L) "" else "${Reply.Pool.ARTICLE}:$it")
            }.toMap()
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

    private fun sendMsg(req: CreateArticleReplyReq) {
        val article = ArticleService.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(req.articleId)
        }).articles[req.articleId] ?: return

        val from = userApi.queryById(QueryUserReq().apply {
            this.id = arrayListOf(req.ownerUID)
        }).info[req.ownerUID] ?: UserInfo()

        //关注文章通知
        messageApi.groupcast(GroupcastReq().apply {
            this.type = Message.Type.ToArticle
            this.content = Json.dumps(dict {
                this["from"] = from
                this["article"] = article
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
        messageApi.listcast(ListcastReq().apply {
            this.type = Message.Type.ToReply
            this.content = Json.dumps(dict {
                this["from"] = from
                this["article"] = article
            })
            this.createor = req.ownerUID
            this.acceptor = arrayListOf(req.aliasId)
        })
    }
}

