package tech.kotlin.service.account

import com.relops.snowflake.Snowflake
import tech.kotlin.dao.account.AccountDao
import tech.kotlin.model.domain.Account
import tech.kotlin.model.domain.Session
import tech.kotlin.model.request.CheckTokenReq
import tech.kotlin.model.request.CreateSessionReq
import tech.kotlin.model.response.CheckTokenResp
import tech.kotlin.model.response.CreateSessionResp
import tech.kotlin.utils.algorithm.JWT
import tech.kotlin.utils.exceptions.Err
import tech.kotlin.utils.exceptions.abort
import tech.kotlin.utils.exceptions.check
import tech.kotlin.utils.exceptions.tryExec
import tech.kotlin.utils.log.Log
import tech.kotlin.utils.mysql.Mysql
import tech.kotlin.utils.redis.Redis
import tech.kotlin.utils.serialize.Json
import tech.kotlin.utils.properties.Props
import tech.kotlin.utils.properties.long
import tech.kotlin.utils.properties.str
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object TokenService {

    private val properties: Properties = Props.loads("project.properties")
    private val jwtToken: String = properties str "account.jwt.token"
    private val jwtExpire: Long = properties long "account.jwt.expire"

    private fun key(uid: Long) = "session:$uid"

    //校验用户会话
    fun checkToken(req: CheckTokenReq): CheckTokenResp {
        val content: Session
        try {
            content = JWT.loads<Session>(key = jwtToken, jwt = req.token, expire = jwtExpire)
        } catch (err: JWT.ExpiredError) {
            abort(Err.LOGIN_EXPIRE)//登录会话过期
        } catch (err: JWT.DecodeError) {
            abort(Err.TOKEN_FAIL)//token解析失败，无效的token
        }

        //validate device
        content.check(Err.TOKEN_FAIL) { it.device.isEquals(req.device) }

        //validate session
        val cachedSession = tryExec(Err.TOKEN_FAIL) {
            Json.loads<Session>(Redis read { it.get(key(content.id)) })
        }
        cachedSession.check(Err.TOKEN_FAIL) { it.isEqual(content) }

        //validate account state
        val account = Mysql.read {
            AccountDao.getById(it, content.uid, useCache = true, updateCache = true)
        } ?: abort(Err.TOKEN_FAIL)
        account.check(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        return CheckTokenResp().apply {
            this.account = account
        }
    }

    //创建登录会话
    fun createSession(req: CreateSessionReq): CreateSessionResp {
        val content = Session().apply {
            id = Snowflake(0).next()
            device = req.device
            uid = req.uid
        }

        //invoke account state
        val account = Mysql.read {
            AccountDao.getById(it, req.uid, useCache = false, updateCache = true)
        } ?: abort(Err.USER_NOT_EXISTS)
        account.check(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        //save session in redis
        Redis write {
            it.set(key(content.id), Json.dumps(content))
            val expire = (jwtExpire / 1000).toInt()
            it.expire(key(content.id), expire)
        }

        return CreateSessionResp().apply {
            token = JWT.dumps(key = jwtToken, content = content)
        }
    }
}


