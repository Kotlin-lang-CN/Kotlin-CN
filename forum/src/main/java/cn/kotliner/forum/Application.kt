package cn.kotliner.forum

import cn.kotliner.forum.service.account.api.AccountApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableTransactionManagement
class Application {

    @Bean
    fun getLogger(): Logger = LoggerFactory.getLogger(Application::class.java)

}

fun main(vararg args: String) {
    val context = SpringApplication.run(Application::class.java, *args)
    context.getBean(AccountApi::class.java).initAdmin()
}

