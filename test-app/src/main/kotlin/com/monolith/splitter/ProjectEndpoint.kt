package com.monolith.splitter

import datadog.trace.api.Trace
import io.opentracing.Tracer
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/asset")
@Domain("PROJECT")
class ProjectEndpoint(
    private val mdcProvider: MdcProvider,
    private val tracer: Tracer, // TODO
) {

    @Trace(operationName = "getProject")
    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID): ProjectDto {
        val span = tracer.buildSpan("getProject").start()
        return ProjectDto(id).also {
            val logDomain = mdcProvider.get("domain")
            val logTeam = mdcProvider.get("team")
            log.info("Successfully fetched project " +
                    "- projectId=$id, domain=$logDomain" +
                    ", team=$logTeam")
            span.finish()
        }
    }

    data class ProjectDto(
        val id: UUID,
    )

    private companion object {
        private val log = LoggerFactory.getLogger(ProjectEndpoint::class.java)
    }
}