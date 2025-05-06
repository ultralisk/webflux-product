package com.template.boilerplate.filter

import com.template.boilerplate.common.response.ApiResponse
import com.template.boilerplate.common.response.ProblemDetailEx
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    fun createProblemDetail(
        ex: Exception,
        statusCode: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    ): ProblemDetail {
        val problemDetail = ProblemDetailEx(statusCode, ex)

        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ApiResponse<Nothing> =
        ApiResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            timestamp = Instant.now(),
            message = "Server Error",
            error = createProblemDetail(ex),
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ApiResponse<Nothing> =
        ApiResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            timestamp = Instant.now(),
            message = "Invalid Input",
            error = createProblemDetail(ex, HttpStatus.BAD_REQUEST),
        )
}
