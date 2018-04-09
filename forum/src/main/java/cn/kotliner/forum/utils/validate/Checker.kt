package cn.kotliner.forum.utils.validate

import cn.kotliner.forum.exceptions.Abort

interface Checker {

    @Throws(Abort::class)
    fun check()

}