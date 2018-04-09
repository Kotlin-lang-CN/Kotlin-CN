package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.model.Account
import org.apache.ibatis.annotations.*

@Mapper
interface AccountMapper {


    @Select("SELECT COUNT(*) FROM account")
    fun getCount(): Int

    @Select("""
    SELECT  * FROM account
    WHERE id = #{id}
    LIMIT 1
    """)
    @Results(
            Result(property = "lastLogin", column = "last_login"),
            Result(property = "createTime", column = "create_time")
    )
    fun getById(id: Long): Account?

    @Insert("""
    INSERT INTO account
    VALUES
    (#{id}, #{password}, #{lastLogin}, #{state}, #{role}, #{createTime})
    """)
    fun insert(account: Account)

    @Update("""
    UPDATE account SET
    password = #{password},
    last_login = #{lastLogin},
    state = #{state},
    role = #{role},
    create_time = #{createTime}
    WHERE
    id = #{id}
    """)
    fun update(account: Account)

    @UpdateProvider(type = SQLGen::class, method = "accountUpdateWithArgs")
    fun updateWithArgs(args: Map<String, Any>)

    class SQLGen {

        fun accountUpdateWithArgs(args: Map<String, Any>): String {
            return """
            UPDATE account SET
            ${StringBuilder().apply {
                args.forEach { k, _ -> append("$k = #{$k}, ") }
                setLength(length - ", ".length)
            }}
            WHERE id = #{id}
            """
        }
    }

}