package tech.kotlin.common.rpc.registrator;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public interface ServiceRegistrator {

    /***
     * 获取服务地址
     * @param serviceName 服务名
     * @return 服务地址
     */
    InetSocketAddress getService(@NotNull String serviceName) throws Exception;

    /***
     * 发布服务
     * @param serviceName 服务名
     */
    void publishService(@Nonnull String serviceName, @NotNull String address, int port) throws Exception;

}
