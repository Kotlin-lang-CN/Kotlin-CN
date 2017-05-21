package tech.kotlin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import tech.kotlin.common.utils.map
import tech.kotlin.redis.Redis

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Config : HashMap<String, ProcessConf>() {
    init {
        putAll(map("deploy.json", object : TypeReference<HashMap<String, ProcessConf>>() {}))
    }
}

class ProcessConf {
    @JsonProperty("rpc") var rpc: RpcConf = RpcConf()
    @JsonProperty("redis") var redis: RedisConf = RedisConf()
    @JsonProperty("mysql") var mysql: MySqlConf = MySqlConf()
}

class RpcConf {
    @JsonProperty("host") var host: String = ""
    @JsonProperty("port") var port: Int = 0
}

class RedisConf {
    @JsonProperty("master") var master: RedisNode = RedisNode()
    @JsonProperty("slave") var slave: List<RedisNode> = ArrayList()

    class RedisNode {
        @JsonProperty("host") var host: String = ""
        @JsonProperty("port") var port: Int = 0
        @JsonProperty("max_conn") var maxConn: Int = 8
    }
}

class MySqlConf {
    @JsonProperty("host") var host: String = ""
    @JsonProperty("port") var port: Int = 0
    @JsonProperty("username") var username: String = ""
    @JsonProperty("password") var password: String = ""
}