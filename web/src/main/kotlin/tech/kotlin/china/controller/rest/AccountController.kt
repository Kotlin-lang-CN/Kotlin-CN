package tech.kotlin.china.controller.rest

import com.github.pagehelper.PageHelper
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import tech.kotlin.china.framework.of
import tech.kotlin.china.mapper.AccountMapper
import tech.kotlin.china.model.Account
import tech.kotlin.china.model.AccountForm
import utils.dataflow.Doc
import utils.dataflow.forbid
import utils.dataflow.require
import utils.json.toJson
import utils.map.get
import utils.map.p
import utils.properties.Env
import utils.string.encrypt
import utils.string.isEmailFormat
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
class AccountController : _Rest() {

    val ACCOUNT_PAGE_SIZE = 20 //账号列表的分页大小
    val PASSWORD_SECRET: String by lazy { Env["secret_password"] ?: "L21mVCqxXldrNfaAM0YotRO35FUuP8se" }

    @Doc("会员账号登录")
    @RequestMapping("/account/sign_in", method = arrayOf(POST))
    fun login(@RequestBody form: AccountForm, response: HttpServletResponse) = form.check {
        it.name.require("用户名不是合法的邮箱账号") { it.isEmailFormat() }
        it.name.forbid("用户名过长") { it.length > 100 }
        it.password.require("不合法法的密码长度") { it.length >= 6 && it.length < 100 }
    }.session {
        val account = it.of<AccountMapper>().queryByName(form.name)
                .forbid("该用户不存在") { it == null }!!
                .forbid("用户密码不正确") { !form.password.encrypt(PASSWORD_SECRET).equals(it.password) }
        val token = createToken(uid = account.uid, admin = account.rank == 1, username = form.name)
        response.addCookie(Cookie("kotlin_cn", p("token", token)
                .p("uid", account.uid).p("username", account.name).toJson()))
        p("token", token).p("uid", account.uid)
    }

    @Doc("会员账号注册")
    @RequestMapping("/account/sign_up", method = arrayOf(POST))
    fun register(@RequestBody @Doc("注册请求") form: AccountForm) = form.check {
        it.name.require("用户名不是合法的邮箱账号") { it.isEmailFormat() }
        it.name.forbid("用户名过长") { it.length > 100 }
        it.password.require("不合法的密码长度") { it.length >= 6 && it.length < 100 }
    }.session(transaction = true) {
        val mapper = it.of<AccountMapper>()
        mapper.queryByName(form.name).forbid("该用户已存在") { it != null }
        mapper.registerAccount(p("name", form.name).p("password", form.password.encrypt(PASSWORD_SECRET)))
        val account = mapper.queryByName(form.name).forbid("注册失败") { it == null }!!
        p("uid", account.uid).p("username", account.name)
    }

    @Doc("获得当前注册用户总数")
    @RequestMapping("/account/count", method = arrayOf(GET))
    fun getUserCount() = session { it.of<AccountMapper>().getUserCount() }

    @Doc("查看用户列表")
    @RequestMapping("/account/list/{page}", method = arrayOf(GET))
    fun userList(@PathVariable("page") @Doc("分页") page: Int,
                 @RequestParam("category", defaultValue = "all")
                 @Doc("筛选用户类别(all/admin/disable)") category: String) = check {
        page.require("不合法的页数") { it > 0 }
        category.require("错误的用户类型") { it.equals("all") || it.equals("admin") || it.equals("disable") }
    }.authorized(admin = true).session {
        val mapper = it.of<AccountMapper>()
        PageHelper.startPage<Account>((page - 1) * ACCOUNT_PAGE_SIZE + 1, page * ACCOUNT_PAGE_SIZE)
        when (category) {
            "all" -> mapper.queryUserList()
            "admin" -> mapper.queryAdminList()
            "disable" -> mapper.queryDisabledList()
            else -> null
        }!!.map {
            it[Account::uid, Account::name]
                    .p("rank", if (it.rank == 1) "admin" else "normal")
                    .p("forbidden", if (it.forbidden) "被封禁" else "未封禁")
        }
    }

    @Doc("封禁账号")
    @RequestMapping("/account/{uid}/disable", method = arrayOf(POST))
    fun disableAccount(@PathVariable("uid") @Doc("用户id") uid: Long) = check {
        uid.forbid("不合法的用户id") { uid <= 0 }
    }.authorized(admin = true).session(transaction = true) {
        val mapper = it.of<AccountMapper>()
        mapper.queryByUID(uid).forbid("该用户不存在") { it == null }
        mapper.enableAccount(p("uid", uid).p("forbidden", true))
    }

    @Doc("解封账号")
    @RequestMapping("/account/{uid}/enable", method = arrayOf(POST))
    fun enableAccount(@PathVariable("uid") @Doc("用户id") uid: Long) = check {
        uid.forbid("不合法的用户id") { uid <= 0 }
    }.authorized(admin = true).session(transaction = true) {
        val mapper = it.of<AccountMapper>()
        mapper.queryByUID(uid).forbid("该用户不存在") { it == null }
        mapper.enableAccount(p("uid", uid).p("forbidden", false))
    }
}
