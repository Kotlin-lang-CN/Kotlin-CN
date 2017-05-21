package tech.kotlin.account.test

import org.junit.Test
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.serialize.Json
import tech.kotlin.model.Account
import tech.kotlin.model.Device
import tech.kotlin.service.Node
import tech.kotlin.service.account.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class BasicTest {

    val accountService: AccountService by lazy { Node["account"][AccountService::class.java] }
    val tokenService: TokenService by lazy { Node["account"][TokenService::class.java] }

    @Test
    fun register() {
        val account = TestAccount(System.nanoTime().toString())
        val result = accountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })
        println("register=>${Json.dumps(result)}")

        println("check token=>${Json.dumps(tokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")
    }

    @Test
    fun login() {
        val account = TestAccount(System.nanoTime().toString())
        println("register=>${Json.dumps(accountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        }))}")

        var result = accountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.email
            this.password = account.password
        })

        println("check token=>${Json.dumps(tokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")

        result = accountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.username
            this.password = account.password
        })

        println("check token=>${Json.dumps(tokenService.checkToken(CheckTokenReq().apply {
            this.device = account.device
            this.token = result.token
        }))}")
    }

    @Test
    fun registerAndLogin() {
        val account = TestAccount(System.nanoTime().toString())
        println("register=>${Json.dumps(accountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        }))}")

        /**
         * repeat register with same username
         */
        try {
            accountService.create(CreateAccountReq().apply {
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
            accountService.create(CreateAccountReq().apply {
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
        println(Json.dumps(accountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.email
            this.password = account.password
        })))

        /**
         * login with username
         */
        println(Json.dumps(accountService.loginWithName(LoginReq().apply {
            this.device = account.device
            this.loginName = account.username
            this.password = account.password
        })))
    }

    @Test
    fun freeze() {
        val account = TestAccount(System.nanoTime().toString())
        val result = accountService.create(CreateAccountReq().apply {
            this.username = account.username
            this.password = account.password
            this.email = account.email
            this.device = account.device
        })

        accountService.freeze(FreezeAccountReq().apply {
            this.uid = 6271234829191217152L
            opeation = HashMap<Long, Int>().apply { this[result.account.id] = Account.State.BAN }
        })

        try {
            tokenService.checkToken(CheckTokenReq().apply {
                this.device = account.device
                this.token = result.token
            })
        } catch (err: Abort) {
            assert(err.model.code == Err.UNAUTHORIZED.code)
        }

        accountService.freeze(FreezeAccountReq().apply {
            this.uid = 6271234829191217152L
            opeation = HashMap<Long, Int>().apply { this[result.account.id] = Account.State.NORMAL }
        })

        println(Json.dumps(tokenService.checkToken(CheckTokenReq().apply {
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