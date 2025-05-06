package com.template.boilerplate.util

import jakarta.annotation.PostConstruct
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class ConfigLogger(
    private val env: Environment,
) {
    @PostConstruct
    fun validateConfig() {
        val activeProfiles = getActiveProfiles()

        if (activeProfiles.isEmpty()) {
            throw IllegalStateException("❌ 프로파일이 설정되지 않았습니다! 애플리케이션을 종료합니다.")
        }

        val configFileName = "application-${activeProfiles.first()}.yml"
        val configFilePath = Paths.get("src/main/resources/$configFileName")

        if (!Files.exists(configFilePath)) {
            throw IllegalStateException("❌ 설정 파일 '$configFileName'이 존재하지 않습니다! 애플리케이션을 종료합니다.")
        }

        println("📄 로드된 설정 파일: $configFileName")
    }

    private fun getActiveProfiles(): Array<out String> {
        val activeProfiles = env.activeProfiles
        println("✅ Active Profile: ${activeProfiles.joinToString(", ")}")
        return activeProfiles
    }
}
