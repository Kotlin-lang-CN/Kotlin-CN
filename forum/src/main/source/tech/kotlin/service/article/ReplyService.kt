package tech.kotlin.service.article

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import com.relops.snowflake.Snowflake
import tech.kotlin.dao.article.ReplyDao
import tech.kotlin.model.domain.Reply
import tech.kotlin.model.request.CreateArticleReplyReq
import tech.kotlin.model.request.CreateTextContentReq
import tech.kotlin.model.response.CreateReplyResp
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ReplyService {

    fun create(req: CreateArticleReplyReq): CreateReplyResp {
        if (req.aliasId != 0L) {
            Mysql.read { ReplyDao.getById(it, req.aliasId) } ?: abort(Err.REPLY_NOT_EXISTS, "关联评论不存在")
        }

        val contentId = TextService.createContent(CreateTextContentReq().apply {
            this.serializeId = "s"
            this.content = req.content
        }).id

        val reply = Reply().apply {
            this.id = Snowflake(0).next()
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

    fun changeState(req: ChangeReplyStateReq): EmptyResp {
        Mysql.write { ReplyDao.update(it, req.replyId, args = hashMapOf("state" to "${req.state}")) }
        return EmptyResp()
    }

}

class ChangeReplyStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "评论id")
    @JsonProperty("reply_id")
    var replyId = 0L

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "状态")
    @JsonProperty("state")
    var state: Int = Reply.State.NORMAL

}




