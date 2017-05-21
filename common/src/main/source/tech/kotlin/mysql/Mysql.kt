package tech.kotlin.mysql

import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.utils.prop
import java.io.BufferedReader
import java.io.InputStreamReader


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Mysql {

    val log: Logger = LogManager.getLogger("Mysql")
    lateinit var sqlSessionFactory: SqlSessionFactory

    fun init(initFile: String = "") {
        val resource = "mybatis.xml"
        val inputStream = Resources.getResourceAsStream(resource)
        sqlSessionFactory = SqlSessionFactoryBuilder().build(inputStream, prop("mybatis.properties"))

        if (initFile.isNullOrBlank()) return

        write { session ->
            // Initialize object for ScriptRunner
            val runner = ScriptRunner(session.connection)
            // Give the input file to Reader
            BufferedReader(InputStreamReader(javaClass.classLoader.getResourceAsStream(initFile.trimStart('/')))).use {
                runner.runScript(it)
            }
        }
    }

    fun register(mapper: Class<*>) {
        sqlSessionFactory.configuration.addMapper(mapper)
    }

    inline fun <T> read(defaultError: Err = Err.SYSTEM, action: (SqlSession) -> T): T {
        try {
            return sqlSessionFactory.openSession(true).use(action)
        } catch (error: Abort) {
            throw error
        } catch (error: Exception) {
            log.error(error)
            abort(defaultError)
        }
    }

    inline fun <T> write(defaultError: Err = Err.SYSTEM, action: (SqlSession) -> T): T {
        sqlSessionFactory.openSession(false).use {
            try {
                val result = action(it)
                it.commit()
                return result
            } catch (error: Abort) {
                it.rollback()
                throw error
            } catch (error: Exception) {
                it.rollback()
                log.error(error)
                abort(defaultError)
            }
        }
    }
}