package tech.kotlin.service.account

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.common.rpc.RpcInterface
import tech.kotlin.model.Account
import tech.kotlin.model.UserInfo
import tech.kotlin.service.Service.ACCOUNT_USER

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface UserService {

    @RpcInterface(ACCOUNT_USER)
    fun queryById(req: QueryUserReq): QueryUserResp

}

class QueryUserReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "查询用户id")
    @JsonProperty("id")
    var id: List<Long> = ArrayList()

}

class QueryUserResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("account")
    var account: Map<Long, Account> = HashMap()

    @Protobuf(order = 2, required = true, fieldType = FieldType.MAP, description = "查询结果")
    @JsonProperty("info")
    var info: Map<Long, UserInfo> = HashMap()

}
