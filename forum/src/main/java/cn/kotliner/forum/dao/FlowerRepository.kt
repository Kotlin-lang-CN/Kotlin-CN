package cn.kotliner.forum.dao

import cn.kotliner.forum.dao.mysql.FlowerMapper
import cn.kotliner.forum.dao.redis.FlowerCountCache
import cn.kotliner.forum.dao.redis.FlowerEntityCache
import cn.kotliner.forum.domain.model.Flower
import cn.kotliner.forum.utils.get
import org.apache.ibatis.session.SqlSession
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class FlowerRepository {

    @Resource private lateinit var session: SqlSession
    @Resource private lateinit var count: FlowerCountCache
    @Resource private lateinit var entity: FlowerEntityCache

    fun create(flower: Flower) {
        session[FlowerMapper::class].apply {
            if (query(flower.flowerPoolId, flower.owner) != null) return
            insert(flower)
        }
        count.invalid(flower.flowerPoolId)
        entity.invalid(flower.flowerPoolId, flower.owner)
    }

    fun delete(flowerPoolId: String, owner: Long) {
        session[FlowerMapper::class].apply {
            query(flowerPoolId, owner) ?: return
            delete(flowerPoolId, owner)
        }
        count.invalid(flowerPoolId)
        entity.invalid(flowerPoolId, owner)
    }

    fun count(flowerPoolId: String, useCache: Boolean): Int {
        if (useCache) {
            val flower = count[flowerPoolId]
            if (flower >= 0) return flower
            val result = session[FlowerMapper::class].count(flowerPoolId)
            count[flowerPoolId] = result
            return result
        } else {
            return session[FlowerMapper::class].count(flowerPoolId)
        }
    }

    fun queryFlower(flowerPoolId: String, owner: Long, useCache: Boolean): Flower? {
        if (useCache) {
            val flower = entity[flowerPoolId, owner]
            if (flower != null) return flower
            val result = session[FlowerMapper::class].query(flowerPoolId, owner)
            if (result != null) {
                entity[flowerPoolId, owner] = result
            } else {
                entity.invalid(flowerPoolId, owner)
            }
            return result
        } else {
            return session[FlowerMapper::class].query(flowerPoolId, owner)
        }
    }

}