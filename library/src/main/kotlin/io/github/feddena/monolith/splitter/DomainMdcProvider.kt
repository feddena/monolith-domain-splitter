package io.github.feddena.monolith.splitter

import io.github.feddena.monolith.splitter.DomainTags.DOMAIN
import io.github.feddena.monolith.splitter.DomainTags.TEAM
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DomainMdcProvider(
    private val mdcProvider: MdcProvider,
    private val domainRegistry: DomainRegistry,
) {
    fun putDomainTags(domainValue: DomainValue) {
        mdcProvider.put(DOMAIN, domainValue.lowercaseName())
        mdcProvider.put(TEAM, domainValue.team.lowercaseName())
    }

    fun findCurrentDomainTag(): DomainValue? =
        mdcProvider.get(DOMAIN)
            ?.let { domainName ->
                domainRegistry.fromString(domainName)
                    .also { matched ->
                        if (matched == null) {
                            log.error("Found incorrect domain in MDC - $domainName")
                        }
                    }
            }

    fun clearDomainTags() {
        mdcProvider.remove(DOMAIN)
        mdcProvider.remove(TEAM)
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(DomainMdcProvider::class.java)
    }
}
