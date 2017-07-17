package tech.kotlin.service

import tech.kotlin.common.redis.Redis
import tech.kotlin.common.rpc.Serv
import tech.kotlin.service.account.UserApi
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.domain.EmptyResp
import tech.kotlin.service.domain.UserInfo
import tech.kotlin.service.message.CountGroupReq
import tech.kotlin.service.message.CountGroupResp
import tech.kotlin.service.message.GroupApi
import tech.kotlin.service.message.req.ListGroupReq
import tech.kotlin.service.message.resp.ListGroupResp
import tech.kotlin.service.message.req.GroupReq
import tech.kotlin.service.message.resp.QueryGroupStateResp

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object GroupService : GroupApi {

    val userApi by Serv.bind(UserApi::class, ServDef.ACCOUNT)

    override fun queryGroupState(req: GroupReq): QueryGroupStateResp {
        return QueryGroupStateResp().apply {
            this.result = Redis { redis ->
                req.uid.map { it to redis.sismember("msg:g:${req.groupId}", "$it") }.toMap()
            }
        }
    }

    override fun joinGroup(req: GroupReq): EmptyResp {
        Redis { it.sadd("msg:g:${req.groupId}", *req.uid.map { "$it" }.toTypedArray()) }
        return EmptyResp()
    }

    override fun leaveGroup(req: GroupReq): EmptyResp {
        Redis { it.srem("msg:g:${req.groupId}", *req.uid.map { "$it" }.toTypedArray()) }
        return EmptyResp()
    }

    override fun listGroup(req: ListGroupReq): ListGroupResp {
        val subscriber = Redis {
            it.smembers("msg:g:${req.groupId}").toList().filterIndexed { i, _ ->
                i >= req.offset && i < req.limit + req.offset
            }.map { it.toLong() }
        }
        val users = userApi.queryById(QueryUserReq().apply {
            this.id = subscriber
        }).info
        return ListGroupResp().apply {
            this.user = subscriber.map { users[it] ?: UserInfo() }
        }
    }

    override fun countGroup(req: CountGroupReq): CountGroupResp {
        return CountGroupResp().apply {
            this.result = Redis {
                req.ids.map { group -> group to it.scard("msg:g:$group") }.toMap()
            }
        }
    }


}