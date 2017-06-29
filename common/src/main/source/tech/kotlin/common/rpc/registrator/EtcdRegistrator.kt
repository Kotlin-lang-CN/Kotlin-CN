package tech.kotlin.common.rpc.registrator

import com.xqbase.etcd4j.EtcdClient
import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.HandlerThread
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.exceptions.ServiceNotFound
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.str
import java.net.InetSocketAddress
import java.net.URI
import java.util.*


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class EtcdRegistrator(val properties: Properties) : ServiceRegistrator {

    val handlerThread = HandlerThread("EtcdRegistrator").apply { start() }
    val handler by lazy { Handler(handlerThread.looper) }
    val rand = Random(System.currentTimeMillis())
    val client = EtcdClient(URI.create(properties str "etcd.host"))

    override fun getService(serviceName: String): InetSocketAddress {
        val resp = client.listDir("/service/$serviceName")
        Log.i("EtcdRegistrator",
                "find service $serviceName with etcd response ${Json.dumps(resp)}")
        if (resp.isNotEmpty()) {
            val nodeValue = resp[rand.nextInt(resp.size)].value.split(':')
            val newAddress = InetSocketAddress(nodeValue[0], nodeValue[1].toInt())
            return newAddress
        } else
            throw ServiceNotFound("no such service $serviceName found by etcd")
    }

    override fun publishService(serviceName: String, hostName: String, port: Int) {
        val uuid = UUID.randomUUID().mostSignificantBits
        val ttl = 10000
        object : Runnable {
            override fun run() {
                try {
                    client.set("/service/$serviceName/$uuid", "$hostName:$port", ttl / 1000)
                } catch (error: Throwable) {
                    Log.e(error)
                } finally {
                    handler.postDelay(this, (ttl / 2).toLong())
                }
            }
        }.run()
    }

}

