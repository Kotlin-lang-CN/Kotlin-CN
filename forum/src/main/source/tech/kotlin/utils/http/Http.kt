package tech.kotlin.utils.http

import org.eclipse.jetty.client.HttpClient
import java.net.HttpURLConnection.setFollowRedirects
import org.eclipse.jetty.util.ssl.SslContextFactory


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
object Http : HttpClient(SslContextFactory()) {

    init {
        // Configure HttpClient, for example:
        //isFollowRedirects = false
        // Start HttpClient
        start()
    }

    fun terminate() {
        stop()
    }

}