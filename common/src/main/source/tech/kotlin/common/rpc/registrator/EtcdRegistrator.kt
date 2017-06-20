package tech.kotlin.common.rpc.registrator

import mousio.etcd4j.EtcdClient
import mousio.etcd4j.responses.EtcdKeysResponse
import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.exceptions.ServiceNotFound
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.str
import java.net.InetSocketAddress
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class EtcdRegistrator(val properties: Properties) : ServiceRegistrator {

    val handlerThread = HandlerThread("EtcdRegistrator").apply { start() }
    val handler by lazy { Handler(handlerThread.looper) }
    val rand = Random(System.currentTimeMillis())
    val client = EtcdClient(*ArrayList<String>().apply {
        for (i in 1..100) {
            try {
                add(properties str "etcd.host.$i")
            } catch (abort: Abort) {
                break
            }
        }
    }.map { URI.create(it) }.toTypedArray())

    override fun getService(serviceName: String): InetSocketAddress {
        val resp = client.getDir("/service/$serviceName").send().get()
        Log.i("EtcdRegistrator",
                "find service $serviceName with etcd response ${Json.dumps(resp.node.nodes)}")
        if (resp.node.nodes.isNotEmpty()) {
            val nodeValue = resp.node.nodes[rand.nextInt(resp.node.nodes.size)].value.split(':')
            val newAddress = InetSocketAddress(nodeValue[0], nodeValue[1].toInt())
            return newAddress
        } else
            throw ServiceNotFound("no such service $serviceName found by etcd")
    }

    override fun publishService(serviceName: String, address: InetSocketAddress) {
        val uuid = UUID.randomUUID().mostSignificantBits
        val ttl = 10000
        object : Runnable {
            override fun run() {
                try {
                    client.put("/service/$serviceName/$uuid", "${address.hostName}:${address.port}").ttl(ttl / 1000)
                            .send().get()
                } catch (error: Throwable) {
                    Log.e(error)
                } finally {
                    handler.postDelay(this, (ttl / 2).toLong())
                }
            }
        }.run()
    }

}

