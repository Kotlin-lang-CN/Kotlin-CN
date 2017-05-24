package tech.kotlin.utils.exceptions

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
enum class Err(val code: Int, val msg: String) {
    SYSTEM(1, "系统错误"),
    PARAMETER(2, "参数错误"),

    TOKEN_FAIL(3, "非法请求"),
    UNAUTHORIZED(4, "用户权限限制"),
    LOGIN_EXPIRE(5, "登录过期"),

    USER_NAME_EXISTS(30, "用户已存在"),
    USER_EMAIL_EXISTS(31, "用户邮箱已经存在"),
    USER_NOT_EXISTS(32, "用户不存在"),
    ILLEGAL_PASSWORD(33, "密码错误"),
    ARTICLE_NOT_EXISTS(34, "文章不存在"),
    REPLY_NOT_EXISTS(35, "评论不存在")
}