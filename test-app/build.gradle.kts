plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    // OpenTracing API
    implementation("io.opentracing:opentracing-api:0.33.0")
    implementation("io.opentracing:opentracing-util:0.33.0")
    // Datadog Tracing API
    implementation("com.datadoghq:dd-trace-api:1.34.0")
    implementation("com.datadoghq:dd-trace-ot:1.34.0")
    implementation("jakarta.servlet:jakarta.servlet-api")

    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // tests
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-test")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.monolith.splitter.TestApplicationKt")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("com.monolith.splitter.TestApplicationKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("java.awt.headless", "true")

    // Configure to use Datadog Java agent
    jvmArgs(
        "-javaagent:/opt/dd-java-agent.jar",
        "-Ddd.service=monolith-service-name",
        "-Ddd.profiling.enabled=true",
        "-Ddd.env=test",
        "-Ddd.version=1.0",
    )
}
