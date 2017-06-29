package tech.kotlin.dao

import org.apache.ibatis.annotations.*
import org.apache.ibatis.session.SqlSession
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.service.domain.Flower
import tech.kotlin.common.mysql.Mysql
import tech.kotlin.common.mysql.get

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object FlowerDao {

    init {
        Mysql.register(FlowerMapper::class.java)
    }

    fun create(session: SqlSession, flower: Flower) {
        session[FlowerMapper::class].apply {
            if (query(flower.flowerPoolId, flower.owner) != null) return
            insert(flower)
        }
        Cache.Count.invalid(flower.flowerPoolId)
        Cache.Entity.invalid(flower.flowerPoolId, flower.owner)
    }

    fun delete(session: SqlSession, flowerPoolId: String, owner: Long) {
        session[FlowerMapper::class].apply {
            query(flowerPoolId, owner) ?: return
            delete(flowerPoolId, owner)
        }
        Cache.Count.invalid(flowerPoolId)
        Cache.Entity.invalid(flowerPoolId, owner)
    }

    fun count(session: SqlSession, flowerPoolId: String, cache: Boolean): Int {
        if (cache) {
            val flower = Cache.Count[flowerPoolId]
            if (flower >= 0) return flower
            val result = session[FlowerMapper::class].count(flowerPoolId)
            Cache.Count[flowerPoolId] = result
            return result
        } else {
            return session[FlowerMapper::class].count(flowerPoolId)
        }
    }

    fun queryFlower(session: SqlSession, flowerPoolId: String, owner: Long, cache: Boolean): Flower? {
        if (cache) {
            val flower = Cache.Entity[flowerPoolId, owner]
            if (flower != null) return flower
            val result = session[FlowerMapper::class].query(flowerPoolId, owner)
            if (result != null) {
                Cache.Entity[flowerPoolId, owner] = result
            } else {
                Cache.Entity.invalid(flowerPoolId, owner)
            }
            return result
        } else {
            return session[FlowerMapper::class].query(flowerPoolId, owner)
        }
    }

    private object Cache {

        object Count {

            private fun key(flowerPoolId: String) = "flower:count:$flowerPoolId"

            operator fun set(flowerPoolId: String, count: Int) {
                Redis { it[key(flowerPoolId)] = "$count" }
            }

            operator fun get(flowerPoolId: String): Int {
                return Redis { it[key(flowerPoolId)]?.toInt() ?: -1 }
            }

            fun invalid(flowerPoolId: String) {
                Redis { it.del(key(flowerPoolId)) }
            }
        }

        object Entity {

            private fun key(flowerPoolId: String, owner: Long) = "flower:$owner:$flowerPoolId"

            operator fun set(flowerPoolId: String, owner: Long, flower: Flower) {
                val key = key(flowerPoolId, owner)
                Redis {
                    val map = HashMap<String, String>()
                    Json.reflect(flower) { obj, name, field -> map[name] = "${field.get(obj)}" }
                    it.hmset(key, map)
                    it.expire(key, 24 * 60 * 60)//cache for 24 hours
                }
            }

            operator fun get(flowerPoolId: String, owner: Long): Flower? {
                val flowerMap = Redis { it.hgetAll(key(flowerPoolId, owner)) }
                return if (!flowerMap.isEmpty()) Json.rawConvert<Flower>(flowerMap) else null
            }

            fun invalid(flowerPoolId: String, owner: Long) {
                Redis { it.del(key(flowerPoolId, owner)) }
            }
        }
    }

    interface FlowerMapper {

        @Insert("""
        INSERT INTO flower
        VALUES
        (#{id}, #{flowerPoolId}, #{owner}, #{createTime})
        """)
        fun insert(flower: Flower)

        @Delete("""
        DELETE FROM flower
        WHERE flower_pool_id=#{flower_pool_id}
        AND owner=#{owner}
        """)
        fun delete(@Param("flower_pool_id") flowerPoolId: String,
                   @Param("owner") owner: Long)

        @Select("""
        SELECT COUNT(*) FROM flower
        WHERE flower_pool_id=#{flowerPoolId}
        """)
        fun count(flowerPoolId: String): Int

        @Select("""
        SELECT * FROM flower
        WHERE flower_pool_id=#{flower_pool_id}
        AND owner=#{owner}
        LIMIT 1
        """)
        @Results(Result(column = "flower_pool_id", property = "flowerPoolId"))
        fun query(@Param("flower_pool_id") flowerPoolId: String,
                  @Param("owner") owner: Long): Flower?
    }

}