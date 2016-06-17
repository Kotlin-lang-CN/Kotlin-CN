package tech.kotlin.china.restful.model

open class BaseResponse(val message: String, val status: Int)

class Data(val data: Any) : BaseResponse(message = "", status = 200)

class Success() : BaseResponse(message = "", status = 200)

class Fail(message: String, status: Int = 400) : BaseResponse(message = message, status = status)

