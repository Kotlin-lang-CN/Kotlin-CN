package tech.kotlin.service

import tech.kotlin.common.algorithm.JWT
import tech.kotlin.common.redis.Redis
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.*
import tech.kotlin.dao.AccountDao
import tech.kotlin.service.domain.Account
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.CreateSessionReq
import tech.kotlin.service.account.resp.CheckTokenResp
import tech.kotlin.service.account.resp.CreateSessionResp
import tech.kotlin.service.domain.AccountSession
import tech.kotlin.service.account.SessionApi
import tech.kotlin.common.mysql.*
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object SessionService : SessionApi {

    private val jwtToken: String = Props str "account.jwt.token"
    private val jwtExpire: Long = Props long "account.jwt.expire"

    private fun key(uid: Long) = "session:$uid"

    //创建会话
    override fun createSession(req: CreateSessionReq): CreateSessionResp {
        val content = AccountSession().apply {
            id = IDs.next()
            device = req.device
            uid = req.uid
        }

        //invoke account state
        val account = Mysql.read {
            AccountDao.getById(it, req.uid, useCache = false, updateCache = true)
        } ?: abort(Err.USER_NOT_EXISTS)
        account.check(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        //save session in redis
        Redis {
            it.set(key(content.id), Json.dumps(content))
            val expire = (jwtExpire / 1000).toInt()
            it.expire(key(content.id), expire)
        }

        return CreateSessionResp().apply {
            token = JWT.dumps(key = jwtToken, content = content)
        }
    }

    //校验用户会话
    override fun checkToken(req: CheckTokenReq): CheckTokenResp {
        val content: AccountSession
        try {
            //validate session
            content = JWT.loads<AccountSession>(key = jwtToken, jwt = req.token, expire = jwtExpire)
            tryExec(Err.TOKEN_FAIL) {
                assert(content.device.isEquals(req.device))
                val result = Json.loads<AccountSession>(Redis { it.get(key(content.id)) })
                assert(content.isEqual(result))
                return@tryExec result
            }
        } catch (err: JWT.ExpiredError) {
            abort(Err.LOGIN_EXPIRE)//登录会话过期
        } catch (err: JWT.DecodeError) {
            abort(Err.TOKEN_FAIL)//token解析失败，无效的token
        }

        //validate account state
        val account = Mysql.read { AccountDao.getById(it, content.uid, useCache = true, updateCache = true) }
                ?.check(Err.USER_BAN) { it.state != Account.State.BAN }
                ?: abort(Err.TOKEN_FAIL)

        ////validate user email state
        //val user = Mysql.read { UserInfoDao.getById(it, content.uid, useCache = true, updateCache = true) }
        //        ?.check(Err.NEED_ACTIVATE_EMAIL) { it.emailState != UserInfo.EmailState.VERIFIED }
        //        ?: abort(Err.TOKEN_FAIL)

        return CheckTokenResp().apply {
            this.account = account
            //this.user = user
        }
    }

}


