package tech.kotlin.common.test.rpc

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.Test
import tech.kotlin.common.os.Abort
import tech.kotlin.common.rpc.annotations.RpcInterface
import tech.kotlin.common.rpc.annotations.RpcName
import tech.kotlin.common.rpc.invoker.Consumer
import tech.kotlin.common.rpc.invoker.Provider

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class InvokeTest {

    val provider = Provider()

    init {
        provider.register(TestApi::class.java, TestImpl())
    }

    val consumer = object : Consumer() {
        override fun onProxyStart(serviceName: String, requestId: Long, argument: Any) {
            try {
                val result = provider.invokeNative(serviceName, argument)
                onProxyResult(requestId, result)
            } catch (error: Exception) {
                onProxyResult(requestId, error)
            }
        }
    }
    val service by lazy { consumer.getProxy(TestApi::class.java) }

    @Test(timeout = 500)
    fun test() {
        val result = service.div(TestApi.Req().apply { a = 1; b = 2 })
        println(result.result)
    }

    @Test(timeout = 500, expected = Abort::class)
    fun testToCrash() {
        val result = service.div(TestApi.Req().apply { a = 1; b = 0 })
        println(result.result)
    }

    @RpcName("test")
    interface TestApi {

        @RpcInterface(1)
        fun div(req: Req): Resp

        class Req {
            @Protobuf(order = 1, fieldType = FieldType.UINT64, required = true)
            @JsonProperty("a")
            var a: Long = 0

            @Protobuf(order = 2, fieldType = FieldType.UINT64, required = true)
            @JsonProperty("b")
            var b: Long = 0
        }

        class Resp {
            @Protobuf(order = 1, fieldType = FieldType.UINT64, required = true)
            @JsonProperty("result")
            var result: Long = 0
        }
    }

    class TestImpl : TestApi {
        override fun div(req: TestApi.Req): TestApi.Resp {
            if (req.b == 0L)
                throw Abort(100, "b should not be 0")

            return TestApi.Resp().apply {
                result = req.a / req.b
            }
        }

    }
}