package com.monolith.splitter

import com.monolith.splitter.configuration.DomainTraceInterceptorConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class DomainInterceptorConfigurationImpl: DomainTraceInterceptorConfiguration {
    override fun getServicesToOverride(): Set<String> {
        return setOf("test-app-service")
    }

    override fun getServiceNamePrefix(): String {
        return "monolith-"
    }
}