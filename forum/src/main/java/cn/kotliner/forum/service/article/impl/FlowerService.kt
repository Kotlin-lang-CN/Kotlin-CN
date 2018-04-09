package cn.kotliner.forum.service.article.impl

import cn.kotliner.forum.utils.IDs
import cn.kotliner.forum.dao.FlowerRepository
import cn.kotliner.forum.domain.model.Flower
import cn.kotliner.forum.service.article.api.FlowerApi
import cn.kotliner.forum.service.article.req.CountStarReq
import cn.kotliner.forum.service.article.req.StarReq
import cn.kotliner.forum.service.article.resp.CountStarResp
import cn.kotliner.forum.service.article.resp.QueryStarResp
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
@Service
class FlowerService : FlowerApi {

    @Resource private lateinit var flowerRepo: FlowerRepository

    @Transactional(readOnly = false)
    override fun star(req: StarReq) {
        flowerRepo.create(Flower().apply {
            this.id = IDs.next()
            this.flowerPoolId = req.poolId
            this.createTime = System.currentTimeMillis()
            this.owner = req.owner
        })
    }

    @Transactional(readOnly = false)
    override fun unstar(req: StarReq) {
        flowerRepo.delete(req.poolId, req.owner)
    }

    @Transactional(readOnly = true)
    override fun countStar(req: CountStarReq): CountStarResp {
        val result = HashMap<String, Int>()
        req.poolIds.forEach { id -> result[id] = flowerRepo.count(id, useCache = true) }
        return CountStarResp().apply {
            this.result = result
        }
    }

    @Transactional(readOnly = true)
    override fun queryStar(req: StarReq): QueryStarResp {
        val flower = flowerRepo.queryFlower(req.poolId, req.owner, true)
        return QueryStarResp().apply {
            this.hasStar = flower != null
            this.flower = flower ?: Flower()
        }
    }

}