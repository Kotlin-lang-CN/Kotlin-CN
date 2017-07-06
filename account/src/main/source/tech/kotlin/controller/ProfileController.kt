package tech.kotlin.controller

import spark.Route
import tech.kotlin.common.utils.check
import tech.kotlin.common.utils.ok
import tech.kotlin.common.utils.tryExec
import tech.kotlin.service.Err
import tech.kotlin.service.ProfileService
import tech.kotlin.service.SessionService
import tech.kotlin.service.account.req.CheckTokenReq
import tech.kotlin.service.account.req.QueryUserReq
import tech.kotlin.service.account.req.UpdateProfileReq
import tech.kotlin.service.domain.Account
import tech.kotlin.service.domain.Profile

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object ProfileController {

    val updateProfile = Route { req, _ ->
        val profile = Profile().apply {
            this.uid = req.queryParams("uid")
                    .tryExec(Err.PARAMETER) { it.toLong() }
            this.gender = req.queryParams("gender")
                    .tryExec(Err.PARAMETER) { it.toInt() }
                    .check(Err.PARAMETER, "无效的gender") {
                        it == Profile.Gender.MALE || it == Profile.Gender.FEMALE
                    }
            this.github = req.queryParams("github") ?: ""
            this.blog = req.queryParams("blog") ?: ""
            this.company = req.queryParams("company") ?: ""
            this.location = req.queryParams("location") ?: ""
            this.description = req.queryParams("description") ?: ""
            this.education = req.queryParams("education") ?: ""
        }

        SessionService.checkToken(CheckTokenReq(req)).account.check(Err.UNAUTHORIZED) {
            it.id == profile.uid || it.role == Account.Role.ADMIN
        }
        ProfileService.updateById(UpdateProfileReq().apply {
            this.profile = arrayListOf(profile)
        })
        return@Route ok()
    }

    val getProfile = Route { req, _ ->
        val id = req.queryParams("id").tryExec(Err.PARAMETER) { it.trim().split(",").map { it.toLong() } }
        val resp = ProfileService.queryById(QueryUserReq().apply {
            this.id = id
        })
        return@Route ok {
            it["profile"] = id.map { resp.profile[it] ?: Profile() }
        }
    }

}