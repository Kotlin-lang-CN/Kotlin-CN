package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;
import tech.kotlin.common.rpc.ProtoCode;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * local exception: rpc服务调用超时
 *********************************************************************/
public class ProxyTimeout extends Abort {
    public ProxyTimeout(String message) {
        super(ProtoCode.TIMEOUT, message);
    }
}
