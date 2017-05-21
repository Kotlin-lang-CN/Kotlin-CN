package tech.kotlin.account.service

import com.relops.snowflake.Snowflake
import tech.kotlin.account.dao.AccountDao
import tech.kotlin.common.algorithm.JWT
import tech.kotlin.common.exceptions.Err
import tech.kotlin.common.exceptions.abort
import tech.kotlin.common.exceptions.require
import tech.kotlin.common.exceptions.tryExec
import tech.kotlin.common.serialize.Json
import tech.kotlin.common.utils.long
import tech.kotlin.common.utils.prop
import tech.kotlin.common.utils.str
import tech.kotlin.model.Account
import tech.kotlin.mysql.Mysql
import tech.kotlin.redis.Redis
import tech.kotlin.service.account.*
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TokenServiceImpl : TokenService {

    val properties: Properties = prop("account.properties")
    val jwtToken: String = properties str "account.jwt.token"
    val jwtExpire: Long = properties long "account.jwt.expire"

    private fun key(uid: Long) = "session:$uid"

    override fun checkToken(req: CheckTokenReq): CheckTokenResp {
        val content: SessionContent
        try {
            content = JWT.loads<SessionContent>(key = jwtToken, jwt = req.token, expire = jwtExpire)
        } catch (err: JWT.ExpiredError) {
            abort(Err.LOGIN_EXPIRE)//登录会话过期
        } catch (err: JWT.DecodeError) {
            abort(Err.TOKEN_FAIL)//token解析失败，无效的token
        }

        //validate device
        content.require(Err.TOKEN_FAIL) { it.device.isEquals(req.device) }

        //validate session
        val cachedSession = tryExec(Err.TOKEN_FAIL) {
            Json.loads<SessionContent>(Redis.read { it.get(key(content.id)) })
        }
        cachedSession.require(Err.TOKEN_FAIL) { it.isEqual(content) }

        //validate account state
        val account = Mysql.read {
            AccountDao.getById(it, content.uid, useCache = true, updateCache = true)
        } ?: abort(Err.TOKEN_FAIL)
        account.require(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        return CheckTokenResp().apply {
            this.account = account
        }
    }

    override fun createSession(req: CreateSessionReq): CreateSessionResp {
        val content = SessionContent().apply {
            id = Snowflake(0).next()
            device = req.device
            uid = req.uid
        }

        //get account state
        val account = Mysql.read {
            AccountDao.getById(it, req.uid, useCache = false, updateCache = true)
        } ?: abort(Err.USER_NOT_EXISTS)
        account.require(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        //save session in redis
        Redis.write {
            val pip = it.pipelined()
            pip.set(key(content.id), Json.dumps(content))
            pip.sync()
        }

        return CreateSessionResp().apply {
            token = JWT.dumps(key = jwtToken, content = content)
        }
    }

}