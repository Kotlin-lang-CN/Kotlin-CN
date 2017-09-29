package cn.kotliner.forum.controller

import cn.kotliner.forum.exceptions.Abort
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.utils.algorithm.Json
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.dict
import org.slf4j.Logger
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException


@ControllerAdvice
class ErrorHandler {

    @Resource private lateinit var log: Logger

    @ExceptionHandler(Throwable::class)
    @ResponseBody
    fun debug(error: Throwable, request: HttpServletRequest): Resp {
        log.error(Json.dumps(dict {
            this["url"] = "${request.requestURI} [${request.method.toUpperCase()}]"
            this["header"] = request.headerNames.asSequence().map { it to request.getHeader(it) }.toMap()
            this["param"] = request.parameterNames.asSequence().map { it to request.getParameter(it) }.toMap()
        }), error)
        return dict {
            this["code"] = Err.SYSTEM.code
            this["msg"] = Err.SYSTEM.msg
        }
    }

    @ExceptionHandler(value = *arrayOf(ConstraintViolationException::class))
    @ResponseBody
    fun handleValidationFailure(ex: ConstraintViolationException): Resp {
        return dict {
            this["code"] = Err.PARAMETER.code
            this["msg"] = ex.constraintViolations.firstOrNull()?.message ?: Err.PARAMETER.msg
        }
    }


    @ExceptionHandler(value = *arrayOf(Abort::class))
    @ResponseBody
    fun handleValidationFailure(ex: Abort): Resp {
        return dict {
            this["code"] = ex.model.code
            this["msg"] = ex.model.msg
        }
    }
}