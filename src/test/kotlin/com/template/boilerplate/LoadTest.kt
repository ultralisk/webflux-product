package com.template.boilerplate

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoadTest {

    private val requestCount = AtomicInteger(0)
    private val webClient = WebClient.create("http://localhost:9218/demo/info")

    @Test
    fun `LoadTest - 1000 Request`() {
        val totalRequests = 1000  // 총 요청 개수

        Flux.range(1, totalRequests)
            .delayElements(Duration.ofMillis(5))  // 요청 간격을 설정하여 부하 조정
            .flatMap {
                webClient.get()
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .doOnNext { response ->
                        val count = requestCount.incrementAndGet()
                        println("✅ [$count] 응답 수신: $response")
                    }
            }
            .blockLast()  // 테스트 완료될 때까지 블록
    }
}
