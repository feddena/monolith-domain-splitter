package com.konsus.domaintag.configuration

interface DomainTraceInterceptorConfiguration {
    fun getServicesToOverride(): Set<String>
    fun getServiceNamePrefix(): String
}