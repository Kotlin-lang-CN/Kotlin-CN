package tech.kotlin.common.test.rpc

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.common.os.Abort
import tech.kotlin.common.rpc.Serv
import tech.kotlin.common.rpc.annotations.RpcInterface
import java.net.InetSocketAddress
import java.util.concurrent.Executors

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
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


fun main(args: Array<String>) {
    Serv.init()
    Serv.register(TestApi::class, TestImpl())
    Serv.publish(InetSocketAddress("0.0.0.0", 8900), "test", Executors.newFixedThreadPool(4))
}