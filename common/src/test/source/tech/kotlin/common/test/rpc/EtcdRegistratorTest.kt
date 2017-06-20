package tech.kotlin.common.test.rpc

import org.junit.Test
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.registrator.EtcdRegistrator
import java.net.InetSocketAddress
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class EtcdRegistratorTest {

    @Test
    fun main() {
        val prop = Properties().apply {
            setProperty("etcd.host.1", "http://192.168.1.101:2379")
            setProperty("etcd.host.2", "http://192.168.1.102:2379")
        }
        val ins = EtcdRegistrator(prop)
        for (i in 1..10) {
            ins.publishService("test", InetSocketAddress("192.168.1.10${i % 2 + 1}", 9000 + i))
        }
        for (i in 1..10) {
            ins.getService("test").apply { Log.e("${this.hostName}:${this.port}") }
            Thread.sleep(1000)
        }
    }

}
