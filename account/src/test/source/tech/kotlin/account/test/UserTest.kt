package tech.kotlin.account.test

import org.junit.Test
import tech.kotlin.common.serialize.Json
import tech.kotlin.model.Account
import tech.kotlin.model.Device
import tech.kotlin.service.Node
import tech.kotlin.service.account.AccountService
import tech.kotlin.service.account.CreateAccountReq
import tech.kotlin.service.account.QueryUserReq
import tech.kotlin.service.account.UserService

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class UserTest {

    val accountService: AccountService by lazy { Node["account"][AccountService::class.java] }
    val userService: UserService by lazy { Node["account"][UserService::class.java] }

    @Test
    fun getMyInfo() {
        val account = TestAccount(System.nanoTime().toString())

        val result = accountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })
        println("register=>${Json.dumps(result)}")

        val resp = userService.queryById(QueryUserReq().apply { id = arrayListOf(result.account.id) })
        println("get info=>${Json.dumps(resp)}")

        val respAcc = resp.account[result.account.id]!!
        val respUser = resp.info[result.account.id]!!
        assert(account.username == respUser.username)
        assert(account.email == respUser.email)
        assert(respAcc.password.isNullOrEmpty())
        assert(respAcc.role == Account.Role.NORMAL)
        assert(respAcc.state == Account.State.NORMAL)
    }

    class TestAccount(val key: String) {
        var username: String = "test-username-$key"
        var password: String = "test-password-$key"
        var email: String = "test-$key@163.com"
        val device = Device().apply {
            token = "test-device-$key"
            platform = Device.Platform.ANDROID
            vendor = "test-vendor-$key"
            system = "test-system-$key"
        }
    }
}