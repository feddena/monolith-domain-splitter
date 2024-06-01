package com.konsus.domaintag.configuration

import com.konsus.domaintag.DomainHandlerInterceptor
import com.konsus.domaintag.DomainMdcProvider
import com.konsus.domaintag.DomainProvider
import com.konsus.domaintag.DomainSpanService
import com.konsus.domaintag.MdcProvider
import com.konsus.domaintag.SpanProvider
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