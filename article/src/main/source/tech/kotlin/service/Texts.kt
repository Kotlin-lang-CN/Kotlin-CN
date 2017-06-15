package tech.kotlin.service

import tech.kotlin.service.domain.TextContent
import tech.kotlin.service.article.req.CreateTextContentReq
import tech.kotlin.service.article.req.QueryTextReq
import tech.kotlin.service.article.resp.CreateTextContentResp
import tech.kotlin.service.article.resp.QueryTextResp
import tech.kotlin.common.utils.IDs
import tech.kotlin.dao.TextDao
import tech.kotlin.service.article.TextApi
import tech.kotlin.utils.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 文本服务，用于提供文本的版本管理和内容风控
 *********************************************************************/
object Texts : TextApi {

    //通过id批量查询文本对象
    override fun getById(req: QueryTextReq): QueryTextResp {
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
    override fun createContent(req: CreateTextContentReq): CreateTextContentResp {
        val content = TextContent().apply {
            this.id = IDs.next()
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

