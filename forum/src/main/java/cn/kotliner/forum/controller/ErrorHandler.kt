package cn.kotliner.forum.controller

import cn.kotliner.forum.exceptions.Abort
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.utils.algorithm.JWT
import cn.kotliner.forum.utils.algorithm.Json
import cn.kotliner.forum.utils.dict
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.desc
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException


@ControllerAdvice
class ErrorHandler {

    @Resource private lateinit var log: Logger

    //服务错误
    @ExceptionHandler(Throwable::class)
    @ResponseBody
    fun debug(error: Throwable, request: HttpServletRequest): Resp {
        log.error("[Err] ${request.desc()}", error)
        return dict {
            this["code"] = Err.SYSTEM.code
            this["msg"] = Err.SYSTEM.msg
        }
    }

    //普通业务逻辑错误
    @ExceptionHandler(Abort::class)
    @ResponseBody
    fun abort(abort: Abort, request: HttpServletRequest): Resp {
        log.debug("[Abort] ${request.desc()},business abort with status code ${abort.model.code}", abort)
        return dict {
            this["code"] = abort.model.code
            this["msg"] = abort.model.msg
        }
    }

    //参数错误
    @ExceptionHandler(
            MethodArgumentNotValidException::class,
            MissingServletRequestParameterException::class,
            MethodArgumentTypeMismatchException::class,
            ConstraintViolationException::class
    )
    @ResponseBody
    fun argumentError(exception: Throwable, request: HttpServletRequest): Resp {
        log.info(String.format("[Parameter] ${request.desc()}", exception))
        return dict {
            this["code"] = Err.PARAMETER.code
            this["msg"] = object : Any() {
                internal fun analysis(): String {
                    when (exception) {
                        is MethodArgumentNotValidException -> {
                            val error = exception.bindingResult.fieldErrors[0]
                            return "参数错误, ${error.defaultMessage}(${error.field})"
                        }
                        is MissingServletRequestParameterException -> {
                            return "缺少参数${exception.parameterName}"
                        }
                        is MethodArgumentTypeMismatchException -> {
                            val require = exception.requiredType
                            return "类型错误(${exception.name}: ${if (require == null) "null" else require.simpleName})"
                        }
                        is ConstraintViolationException -> {
                            val violation = exception.constraintViolations.iterator().next()
                            return String.format("参数错误(%s %s %s)",
                                    violation.rootBeanClass.name,
                                    violation.propertyPath,
                                    violation.message)
                        }
                        else -> throw IllegalArgumentException("unknwon type")
                    }
                }
            }.analysis()
        }
    }
}