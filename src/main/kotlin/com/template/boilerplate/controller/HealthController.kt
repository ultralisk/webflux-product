package com.template.boilerplate.controller

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

/*
@RestController
@RequestMapping("/health")
class HealthController {

    @GetMapping
    suspend fun checkHealth(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}
*/

@Configuration
class HealthRouter {
    @Bean
    fun healthRoutes() =
        coRouter {
            GET("/health") { ok().bodyValueAndAwait("OK") }
        }
}
