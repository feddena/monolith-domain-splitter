package com.monolith.splitter

import datadog.trace.api.Trace
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/project")
@Domain("PROJECT")
class ProjectEndpoint(
    private val mdcProvider: MdcProvider,
) {
    @Trace(operationName = "getProject")
    @GetMapping("/{id}")
    fun getProject(
        @PathVariable id: UUID,
    ): ProjectDto {
        return ProjectDto(id).also {
            val logDomain = mdcProvider.get("domain")
            val logTeam = mdcProvider.get("team")
            log.info(
                "Successfully fetched project " +
                    "- projectId=$id" +
                    ", domain=$logDomain" +
                    ", team=$logTeam",
            )
        }
    }

    @Domain("CHAT")
    @Trace(operationName = "getProjectOwner")
    @GetMapping("/{id}/chat")
    fun getProjectOwner(
        @PathVariable id: UUID,
    ): ProjectChatDto {
        return ProjectChatDto(id).also {
            val logDomain = mdcProvider.get("domain")
            val logTeam = mdcProvider.get("team")
            log.info(
                "Successfully fetched project chat " +
                    "- projectId=$id" +
                    ", domain=$logDomain" +
                    ", team=$logTeam",
            )
        }
    }

    data class ProjectDto(
        val id: UUID,
    )

    data class ProjectChatDto(
        val projectId: UUID,
    )

    private companion object {
        private val log = LoggerFactory.getLogger(ProjectEndpoint::class.java)
    }
}
