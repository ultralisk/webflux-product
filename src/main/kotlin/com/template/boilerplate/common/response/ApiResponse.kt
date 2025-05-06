package com.template.boilerplate.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL) // NULL 필드 자동 제거 설정
data class ApiResponse<T>(
    var status: Int = HttpStatus.OK.value(),
    var timestamp: Instant = Instant.now(),
    var message: String = "Success",
    var data: T? = null,
    var error: ProblemDetail? = null, // GCloud와 같이
)
