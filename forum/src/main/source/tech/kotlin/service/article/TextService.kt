package tech.kotlin.service.article

import tech.kotlin.dao.article.TextDao
import tech.kotlin.model.request.QueryTextReq
import tech.kotlin.model.response.QueryTextResp
import tech.kotlin.utils.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TextService {

    fun getById(req: QueryTextReq): QueryTextResp {
        val result = Mysql.read { TextDao.getById(it, req.id) }
        return QueryTextResp().apply {
            if (result != null) this.result = hashMapOf(req.id to result)
        }
    }

}
