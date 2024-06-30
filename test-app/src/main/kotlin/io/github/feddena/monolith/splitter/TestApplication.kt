package io.github.feddena.monolith.splitter

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TestApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestApplication::class.java, *args)
}
