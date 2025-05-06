package com.template.boilerplate.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.time.Instant
import java.time.format.DateTimeFormatter

// -----------------------------------------------------

@Component
class ErrorController {
    suspend fun handleError(request: ServerRequest): ServerResponse {
        val responseData =
            mapOf(
                "title" to "Error Occurred",
                "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "detail" to "Unexpected error",
                "timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
            )
        return ServerResponse
            .status(
                HttpStatus.INTERNAL_SERVER_ERROR,
            ).bodyValueAndAwait(responseData)
    }
}
