package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class ProxyTimeout extends Abort {
    public ProxyTimeout(String message) {
        super(-2, message);
    }
}
