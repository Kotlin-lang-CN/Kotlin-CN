package tech.kotlin.common.rpc;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public interface ProtoCode {

    int UNKNOWN = 0;

    int TIMEOUT = -1;

    int SOCKET_BUSY = -2;

    int NOT_FOUND = -3;

    int PING = -4;

    int PONG = -5;

}
