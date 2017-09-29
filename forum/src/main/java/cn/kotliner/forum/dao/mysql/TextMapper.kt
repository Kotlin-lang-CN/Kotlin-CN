package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.TextContent
import org.apache.ibatis.annotations.*

@Mapper
interface TextMapper {

    @Insert("""
    INSERT INTO text_content
    VALUES
    (#{id}, #{content}, #{serializeId}, #{createTime})
    """)
    fun insert(content: TextContent)

    @Select("""
    SELECT * FROM text_content
    WHERE id = #{id}
    """)
    @Results(
            Result(column = "serialize_id", property = "serializeId"),
            Result(column = "create_time", property = "createTime")
    )
    fun queryById(id: Long): TextContent?

}