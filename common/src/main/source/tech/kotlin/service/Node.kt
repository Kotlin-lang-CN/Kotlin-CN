package tech.kotlin.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import tech.kotlin.common.rpc.Contexts
import tech.kotlin.common.rpc.ServiceContext
import tech.kotlin.common.utils.map
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
    val config: HashMap<String, Address> = map("service.json", object : TypeReference<HashMap<String, Address>>() {})

    @Synchronized
    fun init(name: String) {
        val conf = config[name]!!
        this.executors = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors())
        val node = Contexts.create(name, executors)
        node.listen(InetSocketAddress(conf.host, conf.port))
        nodeMap.put(name, node)
    }

    @Synchronized
    operator fun get(name: String): ServiceContext {
        val cachedNode = nodeMap[name]
        if (cachedNode != null) return cachedNode

        val conf = config[name]!!
        val host = conf.host
        val port = conf.port

        val context: ServiceContext = Contexts.create(name)
        context.connect(InetSocketAddress(host, port), 100)
        nodeMap[name] = context
        return context
    }

    class Address {
        @JsonProperty("host") var host = ""
        @JsonProperty("port") var port = 0
    }
}
