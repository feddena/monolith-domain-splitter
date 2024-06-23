package com.monolith.splitter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import java.util.UUID

@ExtendWith(OutputCaptureExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndpointDomainTest {
    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should use class domain for span when only class is annotated`(output: CapturedOutput) {
        // given
        val id = UUID.randomUUID()
        // when
        restTemplate.getForEntity("http://localhost:$port/project/$id", String::class.java)
        // then
        assertThat(output.out)
            .contains(
                "Successfully set domain and team to spans - domain=project, team=lions, serviceName=monolith-project",
            )
    }

    @Test
    fun `should use class domain for logs when only class is annotated`(output: CapturedOutput) {
        // given
        val id = UUID.randomUUID()
        // when
        restTemplate.getForEntity("http://localhost:$port/project/$id", String::class.java)
        // then
        assertThat(output.out)
            .contains("Successfully fetched project - projectId=$id, domain=project, team=lions")
    }

    @Test
    fun `should use function domain for span when both function and class are annotated`(output: CapturedOutput) {
        // given
        val id = UUID.randomUUID()
        // when
        restTemplate.getForEntity("http://localhost:$port/project/$id/chat", String::class.java)
        // then
        assertThat(output.out)
            .contains(
                "Successfully set domain and team to spans - domain=chat, team=squirrels, serviceName=monolith-chat",
            )
    }

    @Test
    fun `should use function domain for logs when both function and class are annotated`(output: CapturedOutput) {
        // given
        val id = UUID.randomUUID()
        // when
        restTemplate.getForEntity("http://localhost:$port/project/$id/chat", String::class.java)
        // then
        assertThat(output.out)
            .contains("Successfully fetched project chat - projectId=$id, domain=chat, team=squirrels")
    }
}
