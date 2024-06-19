package com.monolith.splitter.configuration

import datadog.opentracing.DDTracer.DDTracerBuilder
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TracerConfiguration {

    @Bean
    fun tracer(): Tracer {
        val tracer = DDTracerBuilder().build()
        GlobalTracer.registerIfAbsent(tracer)
        return tracer
    }
}