package com.template.boilerplate.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import java.net.URI

class ProblemDetailEx(
    statusCode: HttpStatus,
    ex: Exception,
) : ProblemDetail() {
    val exception: String = ex.javaClass.simpleName

    init {
        title = if (statusCode == HttpStatus.BAD_REQUEST) "Invalid Input" else "Server Error"
        detail = ex.localizedMessage ?: "Unexpected error"
        instance =
            if (statusCode ==
                HttpStatus.BAD_REQUEST
            ) {
                URI.create("/api/error")
            } else {
                URI.create("/error")
            }
    }
}
