package com.template.boilerplate.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.template.boilerplate.common.response.ApiResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.serialization.Serializable
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

// -----------------------------------------------------

@Serializable
data class ResponseData(
    val message: String,
    val framework: String,
)

// -----------------------------------------------------

data class ResponseDataByJackson(
    @JsonProperty("message") val message: String,
    @JsonProperty("framework") val framework: String,
)

/*
// RestController 기반
@RestController
@RequestMapping("/demo")
class DemoController(private val nettyConfig: NettyConfig) {

    @GetMapping("/info")
    suspend fun sayHello(): Response = withContext(Dispatchers.Default) {
        Response("Hello from WebFlux with Netty in Kotlin DSL! useWebflux, ioSelectCount: ${nettyConfig.getIOSelectCount()}, ioWorkerCount: ${nettyConfig.getIOWorkerCount()}")
    }

    @GetMapping("/info2")
    suspend fun sayHello2(): ResponseEntity<Response> {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(Response("Hello from WebFlux with Coroutines + CacheControl!"))
    }

    @GetMapping("/hello")
    suspend fun hi(): Response {
        return Response("Hello from Test!")
    }

    @GetMapping("/monotask")
    suspend fun monotask(): Response {
        delay(1000.milliseconds)
        val result = Mono.just("Hello from WebFlux!").awaitSingle()
        return Response(result)
    }
}
*/

@Component
class DemoController {
    suspend fun handleHello(request: ServerRequest): ServerResponse {
        val responseData = "Hello from WebFlux with Netty in Kotlin DSL!"
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }

    suspend fun handleHelloByProblemDetail(request: ServerRequest): ServerResponse {
        val data = "Hello from WebFlux with Netty in Kotlin DSL!"
        val responseData =
            ApiResponse(
                // status = HttpStatus.OK.value(),
                // timestamp = Instant.now(),
                // message = "",
                data = data,
            )
        return ServerResponse.ok().bodyValueAndAwait(responseData)
    }

    suspend fun handleInfo(request: ServerRequest): ServerResponse {
        val responseData =
            ResponseData("Hello from WebFlux with Netty in Kotlin DSL!", "useWebflux")

        // 1. 수동
        // val jsonResponse = Json.encodeToString(responseData) // JSON
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON) // JSON Response
            .bodyValueAndAwait(responseData)
    }

    suspend fun handleInfo2(request: ServerRequest): ServerResponse {
        val responseData =
            ResponseDataByJackson("Hello from WebFlux with Netty in Kotlin DSL!", "useWebflux")

        // 1. 수동
        // val jsonResponse = Json.encodeToString(responseData) // JSON
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON) // JSON Response
            .bodyValueAndAwait(responseData)
    }

    suspend fun handleInfo3(request: ServerRequest): ServerResponse {
        val responseData = "Hello from WebFlux with Coroutines + CacheControl!"
        return ServerResponse
            .ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .bodyValueAndAwait(responseData)
    }

    suspend fun handleMonotask(request: ServerRequest): ServerResponse {
        delay(1000.milliseconds)
        val result = Mono.just("Hello from WebFlux!").awaitSingle()
        return ServerResponse.ok().bodyValueAndAwait(result)
    }
}
