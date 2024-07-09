import java.security.MessageDigest

plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.9.20"
}

object Meta {
    const val DESCRIPTION = "This library is created to help with monitoring of monolith in Datadog by splitting it by Domains"
    const val LICENSE = "MIT License"
    const val LIBRARY_NAME = "monolith-domain-splitter"
    const val GITHUB_REPO = "feddena/monolith-domain-splitter"
    const val VERSION = "0.0.2"
}

group = "io.github.feddena.monolith.splitter"
version = Meta.VERSION

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

    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // tests
    api("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    withJavadocJar()
}

val sourcesJar =
    tasks.named<Jar>("sourcesJar") {
        archiveBaseName.set(Meta.LIBRARY_NAME)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

val javadocJar =
    tasks.named<Jar>("javadocJar") {
        archiveBaseName.set(Meta.LIBRARY_NAME)
        archiveClassifier.set("javadoc")
        from(tasks.named("dokkaHtml"))
    }

val jar =
    tasks.named<Jar>("jar") {
        archiveBaseName.set(Meta.LIBRARY_NAME)
        archiveVersion.set(Meta.VERSION)
        from(sourceSets.main.get().output)
    }

signing {
    val signingKey = providers.environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers.environmentVariable("GPG_SIGNING_PASSPHRASE")
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        sign(publishing.publications)
    }
}

fun File.md5(): String {
    return hashFile(this, "MD5")
}

fun File.sha1(): String {
    return hashFile(this, "SHA-1")
}

fun hashFile(
    file: File,
    algorithm: String,
): String {
    val buffer = ByteArray(1024)
    val digest = MessageDigest.getInstance(algorithm)
    file.inputStream().use {
        var read = it.read(buffer)
        while (read > -1) {
            digest.update(buffer, 0, read)
            read = it.read(buffer)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

tasks.register("generateChecksums") {
    group = "verification"
    description = "Generates MD5 and SHA1 checksums for all artifacts."
    doLast {
        val artifacts =
            listOf(
                file("build/libs/${Meta.LIBRARY_NAME}-${Meta.VERSION}.jar"),
                file("build/libs/${Meta.LIBRARY_NAME}-${Meta.VERSION}-sources.jar"),
                file("build/libs/${Meta.LIBRARY_NAME}-${Meta.VERSION}-javadoc.jar"),
                file("build/publications/mavenJava/pom-default.xml"),
            )

        artifacts.forEach { artifact ->
            if (artifact.exists()) {
                val md5File = file("${artifact.path}.md5")
                val sha1File = file("${artifact.path}.sha1")
                md5File.writeText(artifact.md5())
                sha1File.writeText(artifact.sha1())
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = Meta.LIBRARY_NAME
            artifact(jar.get())
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            pom {
                name.set(project.name)
                description.set(Meta.DESCRIPTION)
                url.set("https://github.com/${Meta.GITHUB_REPO}")
                licenses {
                    license {
                        name.set(Meta.LICENSE)
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("feddena")
                        name.set("Fedor Denisov")
                        email.set("denisovfedor.a@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/${Meta.GITHUB_REPO}.git")
                    developerConnection.set("scm:git:ssh://github.com/${Meta.GITHUB_REPO}.git")
                    url.set("https://github.com/${Meta.GITHUB_REPO}")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("repos"))
        }
    }
}

tasks.named<PublishToMavenLocal>("publishMavenJavaPublicationToMavenLocal") {
    dependsOn(tasks.named("generateChecksums"))
}
