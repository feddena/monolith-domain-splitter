package com.monolith.splitter.configuration

import com.monolith.splitter.DomainHandlerInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Component
class WebMvcConfigurer(
    private val domainHandlerInterceptor: DomainHandlerInterceptor,
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(domainHandlerInterceptor)
    }
}