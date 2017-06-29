package tech.kotlin.common.rpc.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface
RpcInterface {
    int value();

    long timeout() default 10;

    TimeUnit timeoutUnit() default TimeUnit.SECONDS;

}
