package tech.kotlin.test.service.account

import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Device
import tech.kotlin.model.request.CreateAccountReq
import tech.kotlin.model.request.QueryUserReq
import tech.kotlin.service.account.AccountService.create
import tech.kotlin.service.account.UserService
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class UserTest {

    @org.junit.Test
    fun getMyInfo() {
        val account = TestAccount(System.nanoTime().toString())

        val result = create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })
        println("register=>${Json.dumps(result)}")

        val resp = UserService.queryById(QueryUserReq().apply { id = arrayListOf(result.account.id) })
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