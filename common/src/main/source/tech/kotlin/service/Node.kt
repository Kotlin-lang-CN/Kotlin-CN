package tech.kotlin.service

import tech.kotlin.Config
import tech.kotlin.common.rpc.Contexts
import tech.kotlin.common.rpc.ServiceContext
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
//与远程资源节点的连接支持延迟加载
object Node {

    lateinit var executors: ExecutorService
    private val nodeMap = ConcurrentHashMap<String, ServiceContext>()

    @Synchronized
    fun init(name: String) {
        val conf = Config[name]!!.rpc
        val host = conf.host
        val port = conf.port
        this.executors = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors())
        val node = Contexts.create(name, executors)
        node.listen(InetSocketAddress(host, port))
        nodeMap.put(name, node)
    }

    @Synchronized
    operator fun get(name: String): ServiceContext {
        val cachedNode = nodeMap[name]
        if (cachedNode != null) return cachedNode

        val conf = Config[name]!!.rpc
        val host = conf.host
        val port = conf.port

        val context: ServiceContext = Contexts.create(name)
        context.connect(InetSocketAddress(host, port), 100)
        nodeMap[name] = context
        return context
    }
}

object Service {

    const val ACCOUNT_CHECK_TOKEN = 100000
    const val ACCOUNT_CREATE_SESSION = 100001
    const val ACCOUNT_REGISTER = 100002
    const val ACCOUNT_LOGIN = 100003
    const val ACCOUNT_FREEZE = 100004
    const val ACCOUNT_USER = 100005

    const val ARTICLE_CREATE = 100021
    const val ARTICLE_UPDATE = 100022
    const val ARTICLE_CHANGE_STATE = 100023
    const val ARTICLE_QUERY_BY_ID = 100024
    const val ARTICLE_QUERY_IN_ORDER = 100025
}
