package com.template.boilerplate.config

import com.sun.management.OperatingSystemMXBean
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.resources.LoopResources
import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

@Configuration
class NettyConfig {
    @Value("\${reactor.netty.requestRate}")
    private val requestRateValue: Int = 10000

    @Value("\${reactor.netty.ioSelectRateFactor}")
    private val ioSelectRateFactor: Int = 5000

    @Value("\${reactor.netty.ioWorkerRateFactor}")
    private val ioWorkerRateFactor: Int = 1000

    private val requestRate = AtomicInteger(requestRateValue)

    private val ioSelectCount = AtomicInteger(calculateIOSelectCount(requestRate.get()))
    private val ioWorkerCount = AtomicInteger(calculateIOWorkerCount(requestRate.get()))
    private val maxConnections = AtomicInteger(calculateMaxConnections()) // 계산된 maxConnections 적용

    // --------------------------------------------------------------------

    @Bean
    fun loopResources(): LoopResources {
        // Request마다 생성하지 않도록 Singleton으로
        return LoopResources
            .create(
                "reactor-netty",
                ioSelectCount.get(),
                ioWorkerCount.get(),
                true,
            ).apply {
                logCurrentSettings() // 설정 완료 후 자동 로그 출력
            }
    }

    @Bean
    fun eventLoopGroup(): EventLoopGroup = NioEventLoopGroup(ioWorkerCount.get())

    @Bean
    fun nettyCustomizer(): NettyServerCustomizer =
        NettyServerCustomizer { httpServer ->
            // https://sungjk.github.io/2023/08/25/netty-channel.html
            // https://java.msk.ru/experience-in-webflux-netty-highload-optimization/
            // https://yangbongsoo.tistory.com/30
            httpServer
                .idleTimeout(java.time.Duration.ofMinutes(10))
                .option(ChannelOption.SO_BACKLOG, 2048) // 65535
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_RCVBUF, 128 * 1024) // Recv 버퍼 크기, Byte, 필요에 따라 조정
                .childOption(ChannelOption.SO_SNDBUF, 128 * 1024) // Send 버퍼 크기, Byte, 필요에 따라 조정
        }

    // 🔹 요청 수 기반으로 ioSelectCount 계산
    fun calculateIOSelectCount(requestRate: Int): Int {
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return (cpuCores / 2 + requestRate / ioSelectRateFactor)
            .coerceAtLeast(
                1,
            ).coerceAtMost(cpuCores)
    }

    // 🔹 요청 수 기반으로 ioWorkerCount 계산
    fun calculateIOWorkerCount(requestRate: Int): Int {
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return (cpuCores * 2 + requestRate / ioWorkerRateFactor)
            .coerceAtLeast(
                cpuCores,
            ).coerceAtMost(
                cpuCores * 4,
            )
    }

    fun getSystemMemoryGB(): Long {
        val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        return osBean.totalPhysicalMemorySize / (1024 * 1024 * 1024) // 바이트 → GB 변환
    }

    // 🔹 최대 연결 수 계산 (CPU, 메모리, 네트워크 반영)
    private fun calculateMaxConnections(): Int {
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val memoryGB = getSystemMemoryGB().toInt()
        val requestMemoryUsageMB = 1
        val networkBandwidthMbps = 1000
        val averageRequestSizeKB = 100

        return (cpuCores * 2) +
            (memoryGB * 1024 / requestMemoryUsageMB) +
            (networkBandwidthMbps * 1000 / averageRequestSizeKB)
    }

    /*
    // 🔹 요청 수를 측정하여 자동 적용
    fun calculateIOCountsByRequests(): Pair<Int, Int> {
        val ioSelectCount = calculateIOSelectCount(requestRate.get())
        val ioWorkerCount = calculateIOWorkerCount(requestRate.get())

        return Pair(ioSelectCount, ioWorkerCount)
    }

    // 🔹 요청 수에 따라 IOCounts 자동 조정
    fun updateRequestRate(newRate: Int) {
        requestRate.set(newRate)  // 요청 수 업데이트
        ioSelectCount.set(calculateIOSelectCount(requestRate.get()))  // 동적 반영
        ioWorkerCount.set(calculateIOWorkerCount(requestRate.get()))
        logCurrentSettings() // 설정 변경 후 자동 출력
    }

    @Scheduled(fixedDelay = 5000) // 5초마다 요청 수 확인 후 업데이트
    fun dynamicRequestAdjuster() {
        val currentRequests = requestRate.get() // 현재 요청 수 확인
        val newRate = (currentRequests * 1.1).toInt() // 요청 증가율 적용
        updateRequestRate(newRate) // 동적으로 조정
    }
     */

    private fun logCurrentSettings() {
        println("⚡ Netty 설정!")
        println("   - requestRate: ${requestRate.get()}")
        println("   - ioSelectCount: ${ioSelectCount.get()}")
        println("   - ioWorkerCount: ${ioWorkerCount.get()}")
        println("   - maxConnections: ${maxConnections.get()}")
    }

    fun getRequestRate(): Int = requestRate.get()

    fun getIOSelectCount(): Int = ioSelectCount.get()

    fun getIOWorkerCount(): Int = ioWorkerCount.get()

    fun getMaxConnections(): Int = maxConnections.get()
}
