package cn.kotliner.forum.service.article.impl

import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.dao.TextRepository
import cn.kotliner.forum.domain.TextContent
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.service.article.req.CreateTextContentReq
import cn.kotliner.forum.service.article.req.QueryTextReq
import cn.kotliner.forum.service.article.resp.CreateTextContentResp
import cn.kotliner.forum.service.article.resp.QueryTextResp
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 文本服务，用于提供文本的版本管理和内容风控
 *********************************************************************/
@Service
class TextService : TextApi {

    @Resource private lateinit var textRepo: TextRepository

    //通过id批量查询文本对象
    @Transactional(readOnly = true)
    override fun getById(req: QueryTextReq): QueryTextResp {
        val result = HashMap<Long, TextContent>()
        req.id.forEach {
            val content = textRepo.getById(it) ?: return@forEach
            result[it] = content
        }
        return QueryTextResp().apply {
            this.result = result
        }
    }

    //创建文本对象
    @Transactional(readOnly = false)
    override fun createContent(req: CreateTextContentReq): CreateTextContentResp {
        val content = TextContent().apply {
            this.id = IDs.next()
            this.content = req.content
            this.serializeId = req.serializeId
            this.createTime = System.currentTimeMillis()
        }
        textRepo.create(content)
        return CreateTextContentResp().apply {
            this.id = content.id
        }
    }

}

