package io.github.feddena.monolith.splitter.configuration

import org.springframework.context.annotation.Configuration

@Configuration
class DomainInterceptorConfigurationImpl : DomainTraceInterceptorConfiguration {
    override fun getServicesToOverride(): Set<String> {
        return setOf("monolith-service-name")
    }

    override fun getServiceNamePrefix(): String {
        return "monolith-"
    }
}
