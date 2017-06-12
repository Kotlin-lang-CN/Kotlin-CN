package tech.kotlin.service

import org.apache.commons.lang.RandomStringUtils
import tech.kotlin.model.domain.UserInfo
import tech.kotlin.model.request.ActivateEmailReq
import tech.kotlin.model.request.CreateEmailSessionReq
import tech.kotlin.model.response.CreateEmailSessionResp
import tech.kotlin.model.response.EmptyResp
import tech.kotlin.common.utils.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.common.os.Log
import tech.kotlin.utils.Mysql
import tech.kotlin.common.utils.Props
import tech.kotlin.utils.Redis
import tech.kotlin.common.utils.strDict
import tech.kotlin.dao.UserInfoDao

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object EmailActivates {

    val properties = Props.loads("project.properties")

    fun createSession(req: CreateEmailSessionReq): CreateEmailSessionResp {
        val token = RandomStringUtils.randomAscii(16)//TODO: UGLY, WHAT IF HASH CONFLICT?
        Log.i("create", token)
        Redis.write {
            it.hset("email_activate:$token", "uid", "${req.uid}")
            it.hset("email_activate:$token", "email", req.email)
        }
        return CreateEmailSessionResp().apply { this.token = token }
    }

    fun activate(req: ActivateEmailReq): EmptyResp {
        Log.i("validate", req.token)
        var uid: Long = 0L
        var email: String = ""
        Redis.read {
            val map = it.hgetAll("email_activate:${req.token}")
            if (map.isEmpty()) abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)
            uid = map["uid"]!!.toLong()
            email = map["email"]!!
        }
        Mysql.write {
            val user = UserInfoDao.getById(it, uid) ?: abort(Err.USER_NOT_EXISTS)
            if (user.email != email) abort(Err.ILLEGAL_EMAIL_ACTIVATE_CODE)
            if (user.emailState == UserInfo.EmailState.TO_BE_VERIFY) {
                UserInfoDao.update(it, uid, strDict {
                    this["email_state"] = "${UserInfo.EmailState.VERIFIED}"
                })
            }
        }
        return EmptyResp()
    }

}