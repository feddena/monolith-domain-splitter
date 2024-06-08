package com.monolith.splitter

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/asset")
@Domain("PROJECT")
class ProjectEndpoint {

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID): ProjectDto {
        return ProjectDto(id).also {
            log.info("Successfully fetched project - projectId=$id")
        }
    }

    data class ProjectDto(
        val id: UUID,
    )

    private companion object {
        private val log = LoggerFactory.getLogger(ProjectEndpoint::class.java)
    }
}