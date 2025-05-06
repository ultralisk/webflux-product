package com.template.boilerplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.WebFluxConfigurer

@SpringBootApplication
@ComponentScan(basePackages = ["com.template.boilerplate"])
class DemoApplication {
    @Bean
    fun webFluxConfigurer(): WebFluxConfigurer = object : WebFluxConfigurer {}
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
