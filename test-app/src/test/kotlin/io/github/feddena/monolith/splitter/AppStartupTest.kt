package io.github.feddena.monolith.splitter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppStartupTest {
    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `app start successfully`() {
        // Verify the context loads by checking the health endpoint
        val healthEndpoint = "http://localhost:$port/actuator/health"
        val response = restTemplate.getForEntity(healthEndpoint, String::class.java)
        assertEquals(200, response.statusCodeValue)
    }
}
