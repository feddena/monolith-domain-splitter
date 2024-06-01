package com.monolith.splitter.configuration

interface DomainTraceInterceptorConfiguration {
    fun getServicesToOverride(): Set<String>
    fun getServiceNamePrefix(): String
}