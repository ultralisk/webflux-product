package com.template.boilerplate.filter

/*
@Component
class LoggingFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ) = mono {
        val cachedBody = exchange.request.body.cache()
        val joinedBuffer = DataBufferUtils.join(cachedBody).awaitFirstOrNull()

        val bodyString =
            joinedBuffer?.let { buffer ->
                val bytes = ByteArray(buffer.readableByteCount())
                buffer.read(bytes)
                DataBufferUtils.release(buffer) // 버퍼 즉시 해제
                String(bytes, StandardCharsets.UTF_8)
            } ?: ""
        logger.info("Request Body: $bodyString")

        val bodyFlux =
            if (joinedBuffer != null) {
                Flux.just(joinedBuffer).filter { it != null } // Reactor 스타일 필터링
            } else {
                Flux.empty()
            }

        val mutatedExchange =
            exchange
                .mutate()
                .request(
                    object : ServerHttpRequestDecorator(exchange.request) {
                        override fun getBody(): Flux<DataBuffer?> = bodyFlux
                    },
                ).build()

        chain.filter(mutatedExchange).awaitFirstOrNull()
    }
}
*/
