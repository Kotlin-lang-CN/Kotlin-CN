package tech.kotlin.test.service.account

import org.junit.Test
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Device
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.model.request.CreateAccountReq
import tech.kotlin.model.request.FreezeAccountReq
import tech.kotlin.model.request.LoginReq
import tech.kotlin.service.account.AccountService
import tech.kotlin.service.account.TokenService
import tech.kotlin.utils.exceptions.Abort
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.serialize.Json

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class BasicTest {

    @Test
    fun register() {
        val account = TestAccount(System.nanoTime().toString())
        val result = AccountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })
        println("register=>${Json.dumps(result)}")

        println("check token=>${Json.dumps(TokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")
    }

    @Test
    fun login() {
        val account = TestAccount(System.nanoTime().toString())
        println("register=>${Json.dumps(AccountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        }))}")

        var result = AccountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.email
            this.password = account.password
        })

        println("check token=>${Json.dumps(TokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")

        result = AccountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.username
            this.password = account.password
        })

        println("check token=>${Json.dumps(TokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")
    }

    @Test
    fun registerAndLogin() {
        val account = TestAccount(System.nanoTime().toString())
        println("register=>${Json.dumps(AccountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        }))}")

        /**
         * repeat register with same username
         */
        try {
            AccountService.create(CreateAccountReq().apply {
                this.username = account.username
                this.password = account.password
                this.email = "${account.email}.1"
                this.device = account.device
            })
        } catch (abort: Abort) {
            assert(abort.model.code == Err.USER_NAME_EXISTS.code)
        }

        /**
         * repeat register with same email
         */
        try {
            AccountService.create(CreateAccountReq().apply {
                this.username = "${account.username}.1"
                this.password = account.password
                this.email = account.email
                this.device = account.device
            })
        } catch (abort: Abort) {
            assert(abort.model.code == Err.USER_EMAIL_EXISTS.code)
        }

        /**
         * login with email
         */
        println(Json.dumps(AccountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.email
            this.password = account.password
        })))

        /**
         * login with username
         */
        println(Json.dumps(AccountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.username
            this.password = account.password
        })))
    }

    @Test
    fun freeze() {
        val account = TestAccount(System.nanoTime().toString())
        val result = AccountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })

        AccountService.freeze(FreezeAccountReq().apply {
            this.uid = 6271234829191217152L
            opeation = HashMap<Long, Int>().apply { this[result.account.id] = Account.State.BAN }
        })

        try {
            TokenService.checkToken(CheckTokenReq().apply {
                this.device = account.device
                this.token = result.token
            })
        } catch (err: Abort) {
            assert(err.model.code == Err.UNAUTHORIZED.code)
        }

        AccountService.freeze(FreezeAccountReq().apply {
            this.uid = 6271234829191217152L
            opeation = HashMap<Long, Int>().apply { this[result.account.id] = Account.State.NORMAL }
        })

        println(Json.dumps(TokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        })))
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