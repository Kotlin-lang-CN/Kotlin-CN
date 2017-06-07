package tech.kotlin.common.os;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public interface MsgDispatcher<T> {
    void dispatchMsg(T task);
}
