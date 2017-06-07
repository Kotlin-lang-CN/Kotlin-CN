package tech.kotlin.common.rpc.exceptions;

import tech.kotlin.common.os.Abort;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class NoSuchServiceError extends Abort {
    public NoSuchServiceError(String message) {
        super(-1, message);
    }
}
