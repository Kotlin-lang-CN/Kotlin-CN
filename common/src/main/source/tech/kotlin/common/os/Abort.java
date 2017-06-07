package tech.kotlin.common.os;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.kotlin.common.serialize.Json;

import java.util.Locale;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 带错误码和错误信息的可编码异常
 *********************************************************************/
public class Abort extends RuntimeException {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32, description = "code")
    @JsonProperty("code")
    public int code = 0;

    @Protobuf(order = 2, fieldType = FieldType.STRING, description = "message")
    @JsonProperty("msg")
    public String msg = "unknown error";

    public Abort(int code, String message) {
        super();
        this.code = code;
        this.msg = message;
    }

    @Override
    public String getMessage() {
        return String.format("code=%d; msg='%s'", code, msg);
    }

}
