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

    //通过id批量查询文本对象
    fun getById(req: QueryTextReq): QueryTextResp {
        val result = HashMap<Long, TextContent>()
        Mysql.read { session ->
            req.id.forEach {
                val content = TextDao.getById(session, it) ?: return@forEach
                result[it] = content
            }
        }
        return QueryTextResp().apply {
            this.result = result
        }
    }

    //创建文本对象
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

