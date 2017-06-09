package tech.kotlin.common.test.rpc

import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.common.tcp.TcpPackager
import java.net.InetSocketAddress
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import kotlin.properties.Delegates

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class PackagerTest {

    @Test
    fun test() {
        val rand = Random(System.currentTimeMillis())
        val packageId = UUID.randomUUID().leastSignificantBits
        val type = rand.nextInt()
        val data = ByteArray(1000).apply { rand.nextBytes(this) }
        packager1.write(type, packageId, data)
        packager2.read()
        val packageData = packager2.fetch()[0]
        assert(packageData.length == 1000)
        assert(packageData.type == type)
        assert(packageData.packageId == packageId)
        assert(Arrays.equals(packageData.data, data))
    }


    companion object {

        var packager1 by Delegates.notNull<TcpPackager>()
        var packager2 by Delegates.notNull<TcpPackager>()

        @BeforeClass
        @JvmStatic
        fun before() {
            val server = ServerSocketChannel.open()
            val client1 = SocketChannel.open()
            server.bind(InetSocketAddress(9999))
            client1.connect(InetSocketAddress("localhost", 9999))
            val client2 = server.accept()
            packager1 = TcpPackager(client1, null)
            packager2 = TcpPackager(client2, null)
        }
    }
}