package cn.kotliner.forum.service.message.impl

import cn.kotliner.forum.domain.model.UserInfo
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.message.api.GroupApi
import cn.kotliner.forum.service.message.req.CountGroupReq
import cn.kotliner.forum.service.message.req.GroupReq
import cn.kotliner.forum.service.message.req.ListGroupReq
import cn.kotliner.forum.service.message.resp.CountGroupResp
import cn.kotliner.forum.service.message.resp.ListGroupResp
import cn.kotliner.forum.service.message.resp.QueryGroupStateResp
import cn.kotliner.forum.service.account.req.QueryUserReq
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class GroupService : GroupApi {

    @Resource private lateinit var redis: StringRedisTemplate
    @Resource private lateinit var userApi: UserApi

    override fun queryGroupState(req: GroupReq): QueryGroupStateResp {
        return QueryGroupStateResp().apply {
            this.result = req.uid.map {
                it to redis.boundSetOps("msg:g:${req.groupId}").isMember("$it")
            }.toMap()
        }
    }

    override fun joinGroup(req: GroupReq) {
        redis.boundSetOps("msg:g:${req.groupId}").add(*req.uid.map { "$it" }.toTypedArray())
    }

    override fun leaveGroup(req: GroupReq) {
        redis.boundSetOps("msg:g:${req.groupId}").remove(*req.uid.map { "$it" }.toTypedArray())
    }

    override fun listGroup(req: ListGroupReq): ListGroupResp {
        val subscriber = redis.boundSetOps("msg:g:${req.groupId}")
                .members()
                .filterIndexed { i, _ ->
                    i >= req.offset && i < req.limit + req.offset
                }.map { it.toLong() }

        val users = userApi.queryById(QueryUserReq().apply { this.id = subscriber }).info

        return ListGroupResp().apply {
            this.user = subscriber.map { users[it] ?: UserInfo() }
        }
    }

    override fun countGroup(req: CountGroupReq): CountGroupResp {
        return CountGroupResp().apply {
            this.result = req.ids.map { group -> redis.boundSetOps("msg:g:$group").size() }
        }
    }

}