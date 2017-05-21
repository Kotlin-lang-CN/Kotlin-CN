package tech.kotlin.common.exceptions

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
enum class Err(val code: Int, val msg: String) {
    SYSTEM(1, "系统错误"),
    PARAMETER(2, "参数错误"),
    TOKEN_FAIL(3, "非法请求"),
    UNAUTHORIZED(4, "用户权限限制"),
    USER_NAME_EXISTS(5, "用户已存在"),
    USER_EMAIL_EXISTS(6, "用户邮箱已经存在"),
    USER_NOT_EXISTS(7, "用户不存在"),
    ILLEGAL_PASSWORD(8, "密码错误"),
    LOGIN_EXPIRE(401, "登录过期，或在其他设备上登录"),
}