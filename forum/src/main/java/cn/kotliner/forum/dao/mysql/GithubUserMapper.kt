package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.GithubUser
import org.apache.ibatis.annotations.*

@Mapper
interface GithubUserMapper {

    @Update("""
    UPDATE github_user_info SET
    access_token = #{accessToken},
    name = #{name},
    email = #{email},
    avatar = #{avatar},
    login = #{login},
    blog = #{blog},
    location = #{location},
    follower_count = #{followerCount},
    company = #{company}
    WHERE
    id = #{id}
    """)
    fun update(githubUser: GithubUser)

    @Insert("""
    INSERT INTO github_user_info VALUES
    (#{uid}, #{accessToken}, #{id}, #{name}, #{email}, #{avatar}, #{login}, #{blog},
    #{location}, #{followerCount}, #{company})
    """)
    fun insert(githubUser: GithubUser)

    @Select("""
    SELECT * FROM github_user_info
    WHERE id = #{id}
    """)
    @Results(
            Result(column = "access_token", property = "accessToken"),
            Result(column = "follower_count", property = "followerCount")
    )
    fun getById(id: Long): GithubUser?

}