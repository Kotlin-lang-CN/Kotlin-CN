package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.dao.AccountRepository
import cn.kotliner.forum.service.account.api.SessionApi
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import cn.kotliner.forum.utils.algorithm.JWT
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.req.CheckTokenReq
import cn.kotliner.forum.service.account.req.CreateSessionReq
import cn.kotliner.forum.service.account.resp.CheckTokenResp
import cn.kotliner.forum.service.account.resp.CreateSessionResp
import cn.kotliner.forum.domain.Account
import cn.kotliner.forum.domain.AccountSession
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.utils.*
import cn.kotliner.forum.utils.algorithm.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

@Service
@PropertySource("classpath:forum.properties")
class SessionService : SessionApi {

    @Value("\${account.jwt.token}") private lateinit var jwtToken: String
    @Value("\${account.jwt.expire}") private lateinit var jwtExpire: String

    @Resource private lateinit var repo: AccountRepository
    @Resource private lateinit var redis: StringRedisTemplate

    //创建会话
    @Transactional(readOnly = false)
    override fun createSession(req: CreateSessionReq): CreateSessionResp {
        val content = AccountSession().apply {
            id = IDs.next()
            device = req.device
            uid = req.uid
        }

        //invoke account state
        val account = repo.getById(req.uid, useCache = false) ?: abort(Err.USER_NOT_EXISTS)
        account.check(Err.UNAUTHORIZED) { it.state != Account.State.BAN }

        //save session in redis
        redis.boundValueOps(key(content.id))
                .set(Json.dumps(content), jwtExpire.toLong() / 1000, TimeUnit.SECONDS)

        return CreateSessionResp().apply {
            token = JWT.dumps(key = jwtToken, content = content)
        }
    }

    //校验用户会话
    @Transactional(readOnly = true)
    override fun checkToken(req: CheckTokenReq): CheckTokenResp {
        val content: AccountSession
        try {
            //validate session
            content = JWT.loads<AccountSession>(key = jwtToken, jwt = req.token, expire = jwtExpire.toLong())
            tryExec(Err.TOKEN_FAIL) {
                assert(content.device.isEquals(req.device))
                val result = Json.loads<AccountSession>(redis.boundValueOps(key(content.id)).get())
                assert(content.isEqual(result))
                return@tryExec result
            }
        } catch (err: JWT.ExpiredError) {
            abort(Err.LOGIN_EXPIRE)//登录会话过期
        } catch (err: JWT.DecodeError) {
            abort(Err.TOKEN_FAIL)//token解析失败，无效的token
        }

        //validate account state
        val account = repo.getById(content.uid, useCache = true)
                ?.check(Err.USER_BAN) { it.state != Account.State.BAN }
                ?: abort(Err.TOKEN_FAIL)

        return CheckTokenResp().apply {
            this.account = account
            this.user = user
        }
    }

    private fun key(uid: Long) = "session:$uid"
}