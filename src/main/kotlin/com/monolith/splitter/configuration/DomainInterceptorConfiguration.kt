package com.monolith.splitter.configuration

import com.monolith.splitter.DomainMdcProvider
import com.monolith.splitter.DomainMethodInterceptor
import com.monolith.splitter.DomainProvider
import com.monolith.splitter.DomainSpanService
import com.monolith.splitter.DomainTagsService
import com.monolith.splitter.DomainTraceInterceptor
import datadog.trace.api.GlobalTracer
import org.slf4j.LoggerFactory
import org.springframework.aop.Advisor
import org.springframework.aop.aspectj.AspectJExpressionPointcut
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
class DomainInterceptorConfiguration(
    private val domainTraceInterceptorConfiguration: DomainTraceInterceptorConfiguration,
) {

    init {
        GlobalTracer.get()
            .addTraceInterceptor(DomainTraceInterceptor(domainTraceInterceptorConfiguration))
        log.info("DomainTraceInterceptor is configured")
    }

    @Bean
    internal fun domainTagsService(
        domainSpanService: DomainSpanService,
        domainMdcProvider: DomainMdcProvider
    ) = DomainTagsService(domainSpanService, domainMdcProvider)

    @Bean
    internal fun advisor(
        domainTagsService: DomainTagsService,
        domainProvider: DomainProvider,
    ): Advisor {
        val pointcut = AspectJExpressionPointcut()
        // Match both methods annotated with @Domain and methods inside classes annotated with @Domain.
        // Filter out RestController classes, as they are handled by DomainHandlerInterceptor
        pointcut.expression = "!@within(org.springframework.web.bind.annotation.RestController) &&" +
            "(@annotation(com.konsus.domaintag.Domain) || @within(com.konsus.domaintag.Domain))"
        return DefaultPointcutAdvisor(pointcut, DomainMethodInterceptor(domainTagsService, domainProvider))
            .also { log.info("DomainMethodInterceptor is configured to target both classes and methods") }
    }

    private companion object {
        val log = LoggerFactory.getLogger(DomainInterceptorConfiguration::class.java)
    }
}
