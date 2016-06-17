package tech.kotlin.china.restful.framework

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener

object DBManager : ApplicationListener<ApplicationStartedEvent> {

    val SESSION_FACTORY: SqlSessionFactory by lazy {
        Resources.getResourceAsStream("mybatis-config.xml").use { SqlSessionFactoryBuilder().build(it) }
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        SESSION_FACTORY
    }

    fun <T> dbRead(action: (SqlSession) -> T): T = DBManager.SESSION_FACTORY.openSession(true).use {
        action.invoke(it)
    }

    fun <T> dbWrite(action: (SqlSession) -> T): T = DBManager.SESSION_FACTORY.openSession(false).use {
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


