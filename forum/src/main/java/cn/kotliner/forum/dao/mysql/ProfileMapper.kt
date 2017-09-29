package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.Profile
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

@Mapper
interface ProfileMapper {

    @Select("""
    SELECT * FROM profile
    WHERE uid = #{uid}
    LIMIT 1
    """)
    fun getById(uid: Long): Profile?

    @Insert("""
    INSERT INTO profile VALUES
    (#{uid}, #{gender}, #{github}, #{blog}, #{company}, #{location}, #{description}, #{education})
    """)
    fun insert(profile: Profile)

    @Update("""
    UPDATE profile SET
    gender = #{gender},
    github = #{github},
    blog = #{blog},
    company = #{company},
    location = #{location},
    description = #{description},
    education = #{education}
    WHERE
    uid = #{uid}
    """)
    fun update(profile: Profile)
}