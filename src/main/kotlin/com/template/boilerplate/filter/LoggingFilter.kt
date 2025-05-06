package com.template.boilerplate.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Component
class LoggingFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val request = exchange.request

        return request.body
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                String(bytes, StandardCharsets.UTF_8)
            }.doOnNext { body ->
                logger.info("Request Body: $body")
                println("Request Body: $body")
            }.then(chain.filter(exchange)) // Flux<Void> → Mono<Void> 변환
    }
}
