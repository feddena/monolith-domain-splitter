package com.konsus.domaintag.configuration

import com.konsus.domaintag.DomainMdcProvider
import com.konsus.domaintag.DomainMethodInterceptor
import com.konsus.domaintag.DomainProvider
import com.konsus.domaintag.DomainSpanService
import com.konsus.domaintag.DomainTagsService
import com.konsus.domaintag.DomainTraceInterceptor
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
