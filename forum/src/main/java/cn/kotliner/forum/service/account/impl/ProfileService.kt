package cn.kotliner.forum.service.account.impl

import cn.kotliner.forum.dao.ProfileRepository
import cn.kotliner.forum.domain.Profile
import cn.kotliner.forum.service.account.api.ProfileApi
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.account.req.UpdateProfileReq
import cn.kotliner.forum.service.account.resp.QueryProfileResp
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource

@Component
class ProfileService : ProfileApi {

    @Resource private lateinit var profileRepo: ProfileRepository

    @Transactional(readOnly = true)
    override fun queryById(req: QueryUserReq): QueryProfileResp {
        val result = HashMap<Long, Profile>()
        req.id.forEach {
            result[it] = profileRepo.getById(it, useCache = true) ?: Profile().apply { uid = it }
        }
        return QueryProfileResp().apply {
            this.profile = result
        }
    }

    @Transactional(readOnly = false)
    override fun updateById(req: UpdateProfileReq) {
        req.profile.forEach(profileRepo::createOrUpdate)
    }

}