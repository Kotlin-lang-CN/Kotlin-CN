/***
 * Mysql Database Mapping Logic
 * Library support by Mybatis3: compile 'org.mybatis:mybatis:3.4.0'
 * With plugin Mybatis Page helper: compile 'com.github.pagehelper:pagehelper:4.1.6'
 */
package tech.kotlin.china.database

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.util.*

interface AccountMapper {
    @Select("""
    SELECT * FROM accounts
    WHERE id = #{id}
    """)
    fun queryById(id: Long): HashMap<String, Any?>?

    @Insert("""
    INSERT INTO accounts
    (id, name, token, avatar_url, email)
    VALUES
    (#{id}, #{name}, #{token}, #{avatar_url}, #{email})
    """)
    fun addAccount(data: Map<String, Any?>)

    @Update("""
    UPDATE accounts SET
    name = #{name},
    token = #{token},
    avatar_url = #{avatar_url},
    email = #{email}
    WHERE id = #{id}
    """)
    fun updateAccount(data: Map<String, Any?>)
}

