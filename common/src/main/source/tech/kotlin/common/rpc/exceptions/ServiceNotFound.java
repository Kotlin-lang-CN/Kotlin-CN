package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;
import tech.kotlin.common.rpc.ProtoCode;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * remote exception: 远端未实现调用接口
 *********************************************************************/
public class ServiceNotFound extends Abort {
    public ServiceNotFound(String message) {
        super(ProtoCode.NOT_FOUND, message);
    }
}
