plugins {
    kotlin("jvm") version "1.9.20"
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.opentracing:opentracing-api:0.33.0")
    api("io.opentracing:opentracing-mock:0.33.0")
    api("io.opentracing:opentracing-util:0.33.0")
    implementation("org.springframework:spring-webmvc")

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.slf4j:slf4j-api")
    implementation("com.datadoghq:dd-trace-api:1.34.0")
    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("org.springframework.boot:spring-boot-starter-web")

    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
