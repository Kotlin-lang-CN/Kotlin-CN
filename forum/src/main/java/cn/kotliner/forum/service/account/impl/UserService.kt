package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.dao.AccountRepository
import cn.kotliner.forum.dao.UserRepository
import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.UserInfo
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.service.account.req.ActivateEmailReq
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.account.req.UpdateUserReq
import cn.kotliner.forum.service.account.resp.QueryUserResp
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource

@Service
class UserService : UserApi {

    @Resource private lateinit var accountRepo: AccountRepository
    @Resource private lateinit var userRepo: UserRepository

    //通过id批量查询用户
    @Transactional(readOnly = true)
    override fun queryById(req: QueryUserReq): QueryUserResp {
        val accountMap = HashMap<Long, Account>()
        val userInfoMap = HashMap<Long, UserInfo>()
        req.id.forEach { uid ->
            val account = accountRepo.getById(uid, useCache = true)
            val userInfo = userRepo.getById(uid, useCache = true)
            if (account != null) accountMap[uid] = account
            if (userInfo != null) userInfoMap[uid] = userInfo
        }
        return QueryUserResp().apply {
            account = accountMap
            info = userInfoMap
        }
    }

    //更新用户信息
    @Transactional(readOnly = false)
    override fun updateById(req: UpdateUserReq) {
        val args = HashMap<String, String>().apply { putAll(req.args) }

        if (args.containsKey("username")) {
            val user = userRepo.getByName(args["username"]!!)
            if (user != null) abort(Err.USER_NAME_EXISTS)
        }
        if (args.containsKey("email")) {
            val user = userRepo.getByEmail(args["email"]!!)
            if (user != null) abort(Err.USER_EMAIL_EXISTS)

            if (user?.email == args["email"]) {
                args.remove("email")
            } else {
                args["email_state"] = "${UserInfo.EmailState.TO_BE_VERIFY}"
            }
        }
        userRepo.update(uid = req.id, args = args)
    }

    //激活邮箱
    @Transactional(readOnly = false)
    override fun activateEmail(req: ActivateEmailReq) {
        userRepo.getById(req.uid, useCache = false)
                ?.check(Err.ILLEGAL_EMAIL_ACTIVATE_CODE) { it.email == req.email }
                ?: abort(Err.USER_NOT_EXISTS)
        userRepo.update(req.uid, hashMapOf("email_state" to "${UserInfo.EmailState.VERIFIED}"))
    }

}