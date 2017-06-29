package tech.kotlin.common.http

import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Http : HttpClient(SslContextFactory()) {
    init {
        isFollowRedirects = false
        start()
    }
}