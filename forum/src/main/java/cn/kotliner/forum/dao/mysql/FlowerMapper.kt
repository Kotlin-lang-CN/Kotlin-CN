package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.Flower
import org.apache.ibatis.annotations.*

@Mapper
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
