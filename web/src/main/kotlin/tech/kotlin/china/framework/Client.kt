package tech.kotlin.china.framework

import org.apache.log4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.support.HttpRequestWrapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import utils.dataflow.next
import utils.decorator.ClassDecorator
import utils.decorator.decorate
import utils.properties.Env
import java.lang.reflect.Method
import java.util.*

@Service
class Client {

    val CLIENT_LOG = Env["log"]?.contains("client") == true

    val rest = (if (CLIENT_LOG) RestTemplate() decorate ClientLog else RestTemplate()) next {
        it.interceptors = listOf(ClientHttpRequestInterceptor { request, body, execution ->
            execution.execute(HttpRequestWrapper(request) next {
                it.headers.accept = listOf(MediaType.APPLICATION_JSON)
            }, body)
        })
    }

    inline fun<reified T : Any> get(url: String, params: HashMap<String, Any?> = HashMap()): T {
        val builder = StringBuilder(url);
        if (params.size > 0) {
            builder.append("?")
            params.forEach { k, v -> builder.append(k).append("=").append(v).append("&") }
            builder.setLength(builder.length - 1)
        }
        return rest.getForObject(builder.toString(), T::class.java)
    }

    inline fun<reified T : Any> post(url: String, data: Any? = null, params: HashMap<String, Any?> = HashMap()): T {
        val builder = StringBuilder(url);
        if (params.size > 0) {
            builder.append("?")
            params.forEach { k, v -> builder.append(k).append("=").append(v).append("&") }
            builder.setLength(builder.length - 1)
        }
        return rest.postForObject(builder.toString(), data, T::class.java)
    };
}

object ClientLog : ClassDecorator {

    val log = Logger.getLogger("Client")

    override fun onResult(method: Method, result: Any?, args: Array<Any>): Any? {
        when (method.name) {
            "getForObject" -> log.info("""
                |--- Client [GET] -------------------------------------
                |URL: ${args[0]}
                |Response: $result
                """.trimMargin("|"))
            "postForObject" -> log.info("""
                |--- Client [POST] ------------------------------------
                |URL: ${args[0]}
                |Request: ${args[1]}
                |Response: $result
                """.trimMargin("|"))
        }
        return result
    }

    override fun onError(method: Method, error: Throwable, args: Array<Any>): Throwable {
        when (method.name) {
            "getForObject" -> log.info("""
                |--- Client [GET] -------------------------------------
                |URL: ${args[0]}
                """.trimMargin("|"), error)
            "postForObject" -> log.info("""
                |--- Client [POST] ------------------------------------
                |URL: ${args[0]}
                |Request: ${args[1]}
                """.trimMargin("|"), error)
        }
        return error
    }
}