package tech.kotlin.service

import tech.kotlin.common.utils.IDs
import tech.kotlin.dao.FlowerDao
import tech.kotlin.service.article.FlowerApi
import tech.kotlin.service.article.req.CountStarReq
import tech.kotlin.service.article.req.StarReq
import tech.kotlin.service.article.resp.CountStarResp
import tech.kotlin.service.article.resp.QueryStarResp
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.domain.Flower
import tech.kotlin.common.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object FlowerService : FlowerApi {

    override fun star(req: StarReq): EmptyResp {
        Mysql.write {
            FlowerDao.create(it, Flower().apply {
                this.id = IDs.next()
                this.flowerPoolId = req.poolId
                this.createTime = System.currentTimeMillis()
                this.owner = req.owner
            })
        }
        return EmptyResp()
    }

    override fun unstar(req: StarReq): EmptyResp {
        Mysql.write {
            FlowerDao.delete(it, req.poolId, req.owner)
        }
        return EmptyResp()
    }

    override fun countStar(req: CountStarReq): CountStarResp {
        val result = HashMap<String, Int>()
        Mysql.read { req.poolIds.forEach { id -> result[id] = FlowerDao.count(it, id, true) } }
        return CountStarResp().apply {
            this.result = result
        }
    }

    override fun queryStar(req: StarReq): QueryStarResp {
        val flower = Mysql.read { FlowerDao.queryFlower(it, req.poolId, req.owner, true) }
        return QueryStarResp().apply {
            this.hasStar = flower != null
            this.flower = flower ?: Flower()
        }
    }

}