package io.github.feddena.monolith.splitter

import org.springframework.stereotype.Component

@Component
class DomainRegistry {
    private val nameToDomain = mutableMapOf<String, DomainValue>()

    fun registerDomainValue(domainValue: DomainValue) {
        nameToDomain[domainValue.lowercaseName()] = domainValue
    }

    fun fromString(domain: String): DomainValue? = nameToDomain[domain.lowercase()]
}
