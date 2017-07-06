package tech.kotlin.service

import tech.kotlin.common.mysql.Mysql
import tech.kotlin.dao.ProfileDao
import tech.kotlin.service.account.ProfileApi
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.account.req.UpdateProfileReq
import tech.kotlin.service.account.resp.QueryProfileResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.domain.Profile

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ProfileService : ProfileApi {

    override fun queryById(req: QueryUserReq): QueryProfileResp {
        val result = HashMap<Long, Profile>()
        Mysql.read { session ->
            req.id.forEach {
                result[it] = ProfileDao.getById(session, it, useCache = true) ?: Profile().apply { uid = it }
            }
        }
        return QueryProfileResp().apply {
            this.profile = result
        }
    }

    override fun updateById(req: UpdateProfileReq): EmptyResp {
        Mysql.write { session -> req.profile.forEach { ProfileDao.createOrUpdate(session, it) } }
        return EmptyResp()
    }

}