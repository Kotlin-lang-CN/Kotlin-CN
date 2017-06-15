package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;
import tech.kotlin.common.rpc.ProtoCode;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * local exception: socket缓存空间满，rpc调用繁忙
 *********************************************************************/
public class ServiceBusy extends Abort {
    public ServiceBusy(String message) {
        super(ProtoCode.SOCKET_BUSY, message);
    }
}
