package tech.kotlin.service.article

import com.relops.snowflake.Snowflake
import tech.kotlin.dao.article.TextDao
import tech.kotlin.model.domain.TextContent
import tech.kotlin.model.request.CreateTextContentReq
import tech.kotlin.model.request.QueryTextReq
import tech.kotlin.model.response.CreateTextContentResp
import tech.kotlin.model.response.QueryTextResp
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 文本服务，用于提供文本的版本管理和内容风控
 *********************************************************************/
object TextService {

    fun getById(req: QueryTextReq): QueryTextResp {
        val result = Mysql.read { TextDao.getById(it, req.id) }
        return QueryTextResp().apply {
            if (result != null) this.result = hashMapOf(req.id to result)
        }
    }

    fun createContent(req: CreateTextContentReq): CreateTextContentResp {
        val content = TextContent().apply {
            this.id = Snowflake(0).next()
            this.content = req.content
            this.serializeId = req.serializeId
            this.createTime = System.currentTimeMillis()
        }
        Mysql.write { TextDao.create(it, content) }
        return CreateTextContentResp().apply {
            this.id = content.id
        }
    }

}

