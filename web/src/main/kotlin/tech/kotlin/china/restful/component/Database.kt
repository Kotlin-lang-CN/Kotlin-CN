package tech.kotlin.china.restful.component

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.springframework.stereotype.Component

@Component
open class Database {

    val SESSION_FACTORY: SqlSessionFactory = Resources.getResourceAsStream("database/mybatis-config.xml")
            .use { SqlSessionFactoryBuilder().build(it) }

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