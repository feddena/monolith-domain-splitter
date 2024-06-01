package com.monolith.splitter.configuration

import com.monolith.splitter.DomainHandlerInterceptor
import com.monolith.splitter.DomainMdcProvider
import com.monolith.splitter.DomainProvider
import com.monolith.splitter.DomainSpanService
import com.monolith.splitter.MdcProvider
import com.monolith.splitter.SpanProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
class DomainHandlerInterceptorConfiguration {

    @Bean
    internal fun domainHandlerInterceptor(
        domainMdcProvider: DomainMdcProvider,
        domainSpanService: DomainSpanService,
        domainProvider: DomainProvider
    ) = DomainHandlerInterceptor(
        domainMdcProvider,
        domainSpanService,
        domainProvider
    )

    @Bean
    internal fun domainMdcProvider(mdcProvider: MdcProvider) = DomainMdcProvider(mdcProvider)

    @Bean
    internal fun domainSpanService(spanProvider: SpanProvider) = DomainSpanService(spanProvider)

    @Bean
    internal fun domainProvider() = DomainProvider()

    @Bean
    internal fun spanProvider() = SpanProvider()

    @Bean
    internal fun mdcProvider() = MdcProvider()
}