package tech.kotlin.test.redis

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import tech.kotlin.model.Account
import tech.kotlin.common.serialize.Json
import tech.kotlin.redis.Redis
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class RedisDaoTest {

    val counter = AtomicInteger(1)
    var assertCount = 0

    @Test
    fun transactionTest() {
        val n = 1_000_000
        for (i in 1..n) {
            executor.submit {
                try {
                    val sample = samples[rand.nextInt(samples.size)]
                    AccountTestDao.insertOrUpdate(sample)
                } catch (error: Exception) {
                    error.printStackTrace()
                } finally {
                    counter.incrementAndGet()
                }
            }
        }
        do {
            //检测更新操作的原子性
            val first = AccountTestDao.get(1)
            assert(first == null || first.isEqualTo(samples[0]) || first.isEqualTo(samples[1]))
            assertCount++
            val second = AccountTestDao.get(2)
            assert(second == null || second.isEqualTo(samples[2]) || second.isEqualTo(samples[3]))
            assertCount++
        } while (counter.get() < n)
        println("transaction test pass within: $assertCount asserts!")
    }

    companion object {

        lateinit var executor: ExecutorService
        val rand = Random(System.currentTimeMillis())
        val samples = arrayOf(
                Json.loads<Account>("""
                {
                    "id": 1,
                    "password": "111",
                    "last_login": 1,
                    "state": 1,
                    "role": 1,
                    "create_time": 111
                }
                """), Json.loads<Account>("""
                {
                    "id": 1,
                    "password": "222",
                    "last_login": 2,
                    "state": 0,
                    "role": 0,
                    "create_time": 222
                }
                """), Json.loads<Account>("""
                {
                    "id": 2,
                    "password": "111",
                    "last_login": 1,
                    "state": 1,
                    "role": 1,
                    "create_time": 111
                }
                """), Json.loads<Account>("""
                {
                    "id": 2,
                    "password": "222",
                    "last_login": 2,
                    "state": 0,
                    "role": 0,
                    "create_time": 222
                }
                """)
        )

        @JvmStatic @BeforeClass
        fun before() {
            executor = Executors.newFixedThreadPool(20)
            Redis.init("account")
        }

        @JvmStatic @AfterClass
        fun after() {
            executor.shutdownNow()
        }
    }

    object AccountTestDao {

        fun key(uid: Long) = "account:test:$uid"

        fun get(uid: Long): Account? {
            val account = Redis read { it.hgetAll(key(uid)) }
            if (!account.isEmpty()) {
                return Json.rawConvert<Account>(account)
            } else {
                return null
            }
        }

        fun insertOrUpdate(account: Account) {
            val key = key(account.id)
            Redis write {
                val pip = it.pipelined()
                Json.reflect(account) { obj, name, field -> pip.hset(key, name, "${field.get(obj)}") }
                pip.sync()
            }
        }

    }
}

