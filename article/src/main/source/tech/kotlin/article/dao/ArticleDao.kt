package tech.kotlin.article.dao

import org.apache.ibatis.session.SqlSession
import tech.kotlin.mysql.Mysql

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ArticleDao {

    init {
        Mysql.register(ArticleMapper::class.java)
    }

    fun getById(session: SqlSession, id: Long) {

    }

    interface ArticleMapper {

    }
}