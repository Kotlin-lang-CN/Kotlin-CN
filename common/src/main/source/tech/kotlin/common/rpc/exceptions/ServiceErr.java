package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class ServiceErr extends Abort {

    public ServiceErr(String message) {
        super(-3, message);
    }
}
