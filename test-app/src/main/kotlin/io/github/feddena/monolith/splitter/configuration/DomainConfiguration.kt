package io.github.feddena.monolith.splitter.configuration

import io.github.feddena.monolith.splitter.DomainRegistry
import io.github.feddena.monolith.splitter.DomainValueImpl
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
