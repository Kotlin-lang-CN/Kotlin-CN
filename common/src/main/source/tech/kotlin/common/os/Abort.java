package tech.kotlin.common.os;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.kotlin.common.serialize.Json;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 带错误码和错误信息的可编码异常
 *********************************************************************/
public class Abort extends RuntimeException {

    private final ErrorModel model;

    public Abort(int code, String message) {
        super();
        this.model = new ErrorModel();
        this.model.code = code;
        this.model.message = message;
    }

    public ErrorModel getModel() {
        return model;
    }

    @Override
    public String getMessage() {
        return Json.INSTANCE.dumps(model).trim();
    }

    public static class ErrorModel {
        @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32, description = "code")
        @JsonProperty("code")
        public int code = 0;

        @Protobuf(order = 2, fieldType = FieldType.STRING, description = "message")
        @JsonProperty("msg")
        public String message = "unknown error, please define error as Abort instead";
    }

}
