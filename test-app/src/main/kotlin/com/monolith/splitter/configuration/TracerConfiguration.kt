package com.monolith.splitter.configuration

import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TracerConfiguration {
    @Bean
    fun tracer(): Tracer {
        return GlobalTracer.get()
    }
}
