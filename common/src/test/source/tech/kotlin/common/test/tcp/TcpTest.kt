package tech.kotlin.common.test.tcp

import org.junit.Test
import tech.kotlin.common.os.Handler
import tech.kotlin.common.os.Log
import tech.kotlin.common.os.Looper
import tech.kotlin.common.os.LooperApp
import tech.kotlin.common.tcp.Connection
import tech.kotlin.common.tcp.IOThread
import tech.kotlin.common.tcp.TcpHandler
import tech.kotlin.common.tcp.TcpPackage
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class TcpTest {

    val MAX_CONN = 20
    val PACKAGE_SAMPLE_COUNT = 20_000

    val queue = HashMap<Long, TcpPackage>()
    val conns = ArrayList<Connection>()
    val rand = Random(System.currentTimeMillis())
    val start by lazy {
        for (i in 1..PACKAGE_SAMPLE_COUNT) {
            val bytes = ByteArray(1000).apply { rand.nextBytes(this) }
            val p = TcpPackage(1000, i.toLong(), bytes)
            queue[p.packageId] = p
            conns[rand.nextInt(conns.size)].send(1000, i.toLong(), bytes)
        }
        Unit
    }

    @Test(timeout = 5_000L)
    fun transport() {
        LooperApp.start(LooperApp.AppTask {
            var count = 0
            val sender = IOThread(object : TcpHandler() {
                var connCount = 0
                override fun onConnect(connection: Connection) {
                    Log.i("sender connect")
                    conns.add(connection)
                    connCount++
                    if (connCount == MAX_CONN) start
                }

                override fun onData(connection: Connection, data: TcpPackage) = Unit
                override fun onDisconnected(connection: Connection) {
                    Log.i("sender disconnect")
                }
            })
            val receiver = IOThread(object : TcpHandler() {
                override fun onConnect(connection: Connection) {
                    Log.i("receiver connect")
                }

                override fun onData(connection: Connection, data: TcpPackage) {
                    val origin = queue[data.packageId]!!
                    if (origin.packageId != data.packageId
                            || !Arrays.equals(origin.data, data.data)) {
                        System.err.println("origin:${origin.type},data:${data.type},package is not equal!")
                    }

                    count++
                    println("rate:${count.toFloat() / PACKAGE_SAMPLE_COUNT * 100}%")
                    if (count == PACKAGE_SAMPLE_COUNT) Looper.quiteMain()
                }

                override fun onDisconnected(connection: Connection) {
                    Log.i("receiver disconnected")
                }
            })
            receiver.start()
            sender.start()
            receiver.listen(InetSocketAddress("localhost", 8100))
            for (i in 1..MAX_CONN) {
                sender.connect(InetSocketAddress("localhost", 8100))
            }
        })
    }
}