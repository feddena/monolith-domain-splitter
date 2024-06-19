package com.monolith.splitter.configuration

import com.monolith.splitter.DomainRegistry
import com.monolith.splitter.DomainValueImpl
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class DomainConfiguration(
    private val domainRegistry: DomainRegistry,
) {
    init {
        DomainValueImpl.entries
            .forEach { domainRegistry.registerDomainValue(it) }
    }
}