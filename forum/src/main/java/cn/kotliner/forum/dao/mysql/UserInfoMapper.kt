package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.model.UserInfo
import org.apache.ibatis.annotations.*

@Mapper
interface UserInfoMapper {

    @Select("""
    SELECT * FROM user_info
    WHERE uid = #{uid}
    LIMIT 1
    """)
    @Results(Result(column = "email_state", property = "emailState"))
    fun getById(uid: Long): UserInfo?

    @Select("""
    SELECT * FROM user_info
    WHERE username = #{username}
    LIMIT 1
    """)
    @Results(Result(column = "email_state", property = "emailState"))
    fun getByName(username: String): UserInfo?

    @Select("""
    SELECT * FROM user_info
    WHERE email= #{email}
    LIMIT 1
    """)
    @Results(Result(column = "email_state", property = "emailState"))
    fun getByEmail(email: String): UserInfo?

    @Insert("""
    INSERT INTO user_info
    VALUES
    (#{uid}, #{username}, #{logo}, #{email}, #{emailState})
    """)
    fun insert(user: UserInfo)

    @Update("""
    UPDATE user_info SET
    username = #{username},
    logo = #{logo},
    email = #{email},
    email_state = #{emailState}
    WHERE
    uid = #{uid}
    """)
    fun update(user: UserInfo)

    @UpdateProvider(type = SQLGenerator::class, method = "userUpdateWithArgs")
    fun updateWithArgs(args: Map<String, String>)

    class SQLGenerator {

        fun userUpdateWithArgs(args: Map<String, String>): String {
            return """
            UPDATE user_info SET
            ${StringBuilder().apply {
                args.forEach { k, _ -> append("$k = #{$k}, ") }
                setLength(length - ", ".length)
            }}
            WHERE uid = #{uid}
            """
        }
    }

}