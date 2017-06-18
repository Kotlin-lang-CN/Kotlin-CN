package tech.kotlin.common.rpc.registrator

import tech.kotlin.service.Err
import tech.kotlin.common.utils.abort
import tech.kotlin.common.utils.int
import java.net.InetSocketAddress
import java.util.*

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
class PropRegistrator(val properties: Properties) : ServiceRegistrator {

    override fun publishService(serviceName: String, address: InetSocketAddress) {
        val configPort = properties int "deploy.service.$serviceName.rpc"
        if (address.port != configPort)
            abort(Err.SYSTEM, "service $serviceName should be published at port $configPort")
    }

    override fun getService(serviceName: String): InetSocketAddress {
        val configPort = properties int "deploy.service.$serviceName.rpc"
        return InetSocketAddress("127.0.0.1", configPort)
    }

}