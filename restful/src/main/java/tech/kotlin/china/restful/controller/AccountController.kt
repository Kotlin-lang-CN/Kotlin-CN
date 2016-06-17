package tech.kotlin.china.restful.controller

import com.github.pagehelper.PageHelper
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import tech.kotlin.china.restful.database.AccountMapper
import tech.kotlin.china.restful.database.use
import tech.kotlin.china.restful.model.*
import tech.kotlin.china.restful.utils.*

@RestController
class AccountController : _Base() {

    val ACCOUNT_PAGE_SIZE = 20 //账号列表的分页大小

    @Doc("会员账号登录")
    @RequestMapping("/account/login", method = arrayOf(POST))
    fun login(@RequestBody form: AccountForm) = check(form.name) {
        it.require("用户名不是合法的邮箱账号") { it.isEmailFormat() }
        it.forbid("用户名过长") { it.length > 100 }
    }.check(form.password) {
        it.require("不合法法的密码长度") { it.length >= 6 && it.length < 100 }
    }.session {
        it.use(AccountMapper::class.java) {
            val account = it.queryByName(form.name)
                    .forbid("该用户不存在") { it == null }!!
                    .require("用户密码不正确") { form.password.equals(it.password) }
            Data(Maps.p("token", createToken(uid = account.uid, admin = account.rank == 1))
                    .p("uid", account.uid.toString()))
        }
    }

    @Doc("会员账号注册")
    @RequestMapping("/account/register", method = arrayOf(POST))
    fun register(@RequestBody @Doc("注册请求") form: AccountForm) = check(form.name) {
        it.require("用户名不是合法的邮箱账号") { it.isEmailFormat() }
        it.forbid("用户名过长") { it.length > 100 }
    }.check(form.password) {
        it.require("不合法的密码长度") { it.length >= 6 && it.length < 100 }
    }.session(transaction = true) {
        it.use(AccountMapper::class.java) {
            if (it.queryByName(form.name) != null)
                Fail("该用户已经存在")
            else defaultMode(Fail("注册失败,稍后重试")) {
                it.registerAccount(Maps.p("name", form.name).p("password", form.password))
                Success()
            }
        }
    }

    @Doc("获得当前注册用户总数")
    @RequestMapping("/account/count", method = arrayOf(GET))
    fun getUserCount() = session { it.use(AccountMapper::class.java) { Data(it.getUserCount()) } }

    @Doc("查看用户列表")
    @RequestMapping("/account/list/{page}", method = arrayOf(GET))
    fun userList(@PathVariable("page") @Doc("分页") page: Int, @RequestParam("category", defaultValue = "all")
    @Doc("筛选用户类别(all/admin/disabled)") category: String) = check(this) {
        page.require("不合法的页数") { it > 0 }
        category.require("错误的用户类型") { it.equals("all") || it.equals("admin") || it.equals("disable") }
    }.authorized(admin = true).session {
        it.use(AccountMapper::class.java) {
            PageHelper.startPage<Account>((page - 1) * ACCOUNT_PAGE_SIZE + 1, page * ACCOUNT_PAGE_SIZE)
            Data(when (category) {
                "all" -> it.queryUserList()
                "admin" -> it.queryAdminList()
                "disable" -> it.queryDisabledList()
                else -> null
            }!!.map {
                Maps.p("uid", "${it.uid}").p("username", it.name)
                        .p("rank", if (it.rank == 1) "admin" else "normal")
                        .p("forbidden", if (it.forbidden) "被封禁" else "未封禁")
            })
        }
    }

    @Doc("封禁账号")
    @RequestMapping("/account/{uid}/disable", method = arrayOf(POST))
    fun disableAccount(@PathVariable("uid") @Doc("用户id") uid: Long) = check(uid) {
        it.forbid("不合法的用户id") { uid <= 0 }
    }.authorized(admin = true).session(transaction = true) {
        it.use(AccountMapper::class.java) {
            if (it.queryByUID(uid) == null) Fail("该用户不存在")
            else defaultMode(Fail("操作失败")) {
                it.enableAccount(Maps.p("uid", "$uid").p("forbidden", true))
                Success()
            }
        }
    }

    @Doc("解封账号")
    @RequestMapping("/account/{uid}/enable", method = arrayOf(POST))
    fun enableAccount(@PathVariable("uid") @Doc("用户id") uid: Long) = check(uid) {
        it.forbid("不合法的用户id") { uid <= 0 }
    }.authorized(admin = true).session(transaction = true) {
        it.use(AccountMapper::class.java) {
            if (it.queryByUID(uid) == null) Fail("该用户不存在")
            else defaultMode(Fail("操作失败")) {
                it.enableAccount(Maps.p("uid", "$uid").p("forbidden", false))
                Success()
            }
        }
    }
}
