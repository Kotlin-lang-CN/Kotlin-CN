package tech.kotlin.test.utils.rpc

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.utils.rpc.Contexts
import tech.kotlin.utils.exceptions.Abort
import tech.kotlin.utils.rpc.RpcInterface
import tech.kotlin.utils.rpc.ServiceContext
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class RpcTest {

    @Test
    fun testLocalInvoke() {
        val rand = Random(System.currentTimeMillis())
        for (i in 1..10000) {
            val req = Adder.AdderReq().apply { a = rand.nextInt(100); b = rand.nextInt(100) }
            val resp = localAdder.add(req)
            println("${req.a} + ${req.b} = ${resp.result}")
            assert(req.a + req.b == resp.result)
        }
    }

    @Test
    fun testRemoteInvoke() {
        val rand = Random(System.currentTimeMillis())
        for (i in 1..10000) {
            val req = Adder.AdderReq().apply { a = rand.nextInt(100); b = rand.nextInt(100) }
            val resp = remoteAdder.add(req)
            println("${req.a} + ${req.b} = ${resp.result}")
            assert(req.a + req.b == resp.result)
        }
    }

    @Test(expected = Abort::class)
    fun testThrowRpcError() {
        remoteAdder.add(Adder.AdderReq().apply { a = 100; b = 100 })
    }

    @Test(expected = Abort::class)
    fun testThrowUnknownError() {
        remoteAdder.add(Adder.AdderReq().apply { a = 100; b = 1 })
    }

    companion object {
        lateinit private var node1: ServiceContext
        lateinit private var node2: ServiceContext
        lateinit private var remoteAdder: Adder
        lateinit private var localAdder: Adder
        private val executor = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors())

        @BeforeClass
        @JvmStatic
        fun initRpc() {
            node1 = Contexts.create("node1", executor)
            node2 = Contexts.create("node2")
            node1.register(Adder::class.java, AdderImpl())
            node2.listen(InetSocketAddress("0.0.0.0", 8099))
            node1.connect(InetSocketAddress("localhost", 8099), 1000)
            localAdder = node1.get(Adder::class.java)
            remoteAdder = node2.get(Adder::class.java)
        }

    }
}

interface Adder {

    @RpcInterface(100) fun add(req: AdderReq): AdderResp

    class AdderReq {
        @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32)
        var a: Int = 0

        @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32)
        var b: Int = 0
    }

    class AdderResp {
        @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32)
        var result: Int = 0
    }
}

class AdderImpl : Adder {
    override fun add(req: Adder.AdderReq): Adder.AdderResp {
        if (req.a == 100 && req.b == 100) {
            throw Abort(1090, "fuck!")
        } else if (req.a == 100) {
            throw IOException("fuck up")
        }
        return Adder.AdderResp().apply { result = req.a + req.b }
    }
}
