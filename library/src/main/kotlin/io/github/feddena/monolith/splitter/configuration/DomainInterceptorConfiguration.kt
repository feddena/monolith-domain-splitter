package io.github.feddena.monolith.splitter.configuration

import datadog.trace.api.GlobalTracer
import io.github.feddena.monolith.splitter.DomainMethodInterceptor
import io.github.feddena.monolith.splitter.DomainProvider
import io.github.feddena.monolith.splitter.DomainRegistry
import io.github.feddena.monolith.splitter.DomainTagsService
import io.github.feddena.monolith.splitter.DomainTraceInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.Advisor
import org.springframework.aop.aspectj.AspectJExpressionPointcut
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
open class DomainInterceptorConfiguration(
    private val domainRegistry: DomainRegistry,
    domainTraceInterceptor: DomainTraceInterceptor,
) {
    init {
        GlobalTracer.get()
            .addTraceInterceptor(domainTraceInterceptor)
        log.info("DomainTraceInterceptor is configured")
    }

    @Bean
    open fun advisor(
        domainTagsService: DomainTagsService,
        domainProvider: DomainProvider,
    ): Advisor {
        val pointcut = AspectJExpressionPointcut()
        // Match both methods annotated with @Domain and methods inside classes annotated with @Domain.
        // Filter out RestController classes, as they are handled by DomainHandlerInterceptor
        pointcut.expression = "!@within(org.springframework.web.bind.annotation.RestController) &&" +
            "(@annotation(com.monolith.splitter.Domain) || @within(com.monolith.splitter.Domain))"
        return DefaultPointcutAdvisor(
            pointcut,
            DomainMethodInterceptor(domainTagsService, domainProvider, domainRegistry),
        ).also {
            log.info("DomainMethodInterceptor is configured to target both classes and methods")
        }
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(DomainInterceptorConfiguration::class.java)
    }
}
