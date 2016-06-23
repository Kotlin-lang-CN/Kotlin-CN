package tech.kotlin.china.database

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.springframework.stereotype.Component
import tech.kotlin.china.utils.Env
import tech.kotlin.china.utils.Props
import tech.kotlin.china.utils.p

@Component
open class Database {

    val SESSION_FACTORY: SqlSessionFactory = Resources.getResourceAsStream("mybatis-config.xml").use {
        SqlSessionFactoryBuilder().build(it, Props
                .p("jdbc.driver", Env["jdbc_driver"]) { "com.mysql.jdbc.Driver" }
                .p("jdbc.url", Env["jdbc_url"]) {
                    "jdbc:mysql://localhost:3306/kotlin_china?useUnicode=true&characterEncoding=UTF-8"
                }
                .p("jdbc.username", Env["jdbc_username"]) { "root" }
                .p("jdbc.password", Env["jdbc_password"]) { "root" }
        )
    }

    fun <T> dbRead(action: (SqlSession) -> T): T = SESSION_FACTORY.openSession(true).use { action.invoke(it) }

    fun <T> dbWrite(action: (SqlSession) -> T): T = SESSION_FACTORY.openSession(false).use {
        try {
            val result = action.invoke(it)
            it.commit()
            return result;
        } catch (e: Throwable) {
            it.rollback()
            throw e
        }
    }
}