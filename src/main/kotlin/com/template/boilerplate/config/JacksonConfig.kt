package com.template.boilerplate.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper =
        ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build()) // 최신 Kotlin 지원
            registerModule(JavaTimeModule()) // Instant, LocalDateTime 등 시간 타입 처리

            setSerializationInclusion(JsonInclude.Include.NON_NULL) // NULL 필드 자동 제거
            configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false) // Map에서 NULL 제거

            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // ISO 8601 형식 사용
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 예상치 못한 필드 허용
        }
}
