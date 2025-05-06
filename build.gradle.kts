plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"

    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"

    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17

    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    // maven { url = uri("https://repo.spring.io/milestone") }
    // maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    // implementation("com.google.code.gson:gson")

    // Server(WebFlux or MVC)
    // implementation("org.springframework.boot:spring-boot-starter-web:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.5")

    // Data
    // implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // implementation("org.springframework.boot:spring-boot-starter-data-jdbc") {
    //     exclude(group = "jakarta.servlet")
    // }
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    runtimeOnly("com.h2database:h2")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Development
    // developmentOnly("org.springframework.boot:spring-boot-starter-actuator")
    // developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.0")
    testImplementation("org.springframework:spring-test")
    // testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// kotlin {
// 	compilerOptions {
// 		freeCompilerArgs.addAll("-Xjsr305=strict")
// 	}
// }
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
        incremental = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("verifyKtlint") {
    dependsOn("ktlintCheck")
}
