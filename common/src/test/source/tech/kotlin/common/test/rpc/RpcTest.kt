package tech.kotlin.common.test.rpc

import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.common.os.Abort
import tech.kotlin.common.os.Log
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.registrator.ServiceRegistrator
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class RpcTest {

    @Test
    fun test() {
        val rand = Random(System.currentTimeMillis())
        for (i in 1..10_000) {
            val a = rand.nextLong()
            val b = rand.nextInt().toLong()
            val result = service.div(TestApi.Req().apply { this.a = a; this.b = b })
            println("$a / $b = ${result.result}")
            assert(a / b == result.result)
        }
    }

    @Test
    fun testToCrash() {
        try {
            val result = service.div(TestApi.Req().apply { a = 1; b = 0 })
            println(result.result)
        } catch (err: Abort) {
            Log.i(err)
            assert(err.model.code == 100)
        }
    }

    @Test
    fun multiThreadTest() {
        val executors = Executors.newFixedThreadPool(20)
        val rand = Random(System.currentTimeMillis())
        (1..100_000).map {
            executors.submit {
                try {
                    val a = rand.nextLong()
                    val b = rand.nextInt().toLong()
                    val result = service.div(TestApi.Req().apply { this.a = a; this.b = b })
                    println("${a / b == result.result}\t$a / $b == ${result.result}")
                } catch (error: Throwable) {
                    Log.e(error)
                }
            }
        }.forEach { it.get() }
    }

    companion object {
        val service by Serv.bind(TestApi::class, "test")

        @JvmStatic
        @BeforeClass
        fun before() {
            Serv.init(object : ServiceRegistrator {
                override fun getService(serviceName: String) = InetSocketAddress("127.0.0.1", 8900)
                override fun publishService(serviceName: String, address: InetSocketAddress) = Unit
            })
        }
    }
}