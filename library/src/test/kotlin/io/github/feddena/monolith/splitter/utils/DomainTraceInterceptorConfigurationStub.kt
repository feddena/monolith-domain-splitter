package io.github.feddena.monolith.splitter.utils

import io.github.feddena.monolith.splitter.configuration.DomainTraceInterceptorConfiguration

class DomainTraceInterceptorConfigurationStub : DomainTraceInterceptorConfiguration {
    override fun getServicesToOverride(): Set<String> {
        return setOf("real-service-to-override")
    }

    override fun getServiceNamePrefix(): String {
        return "splitter-"
    }
}
