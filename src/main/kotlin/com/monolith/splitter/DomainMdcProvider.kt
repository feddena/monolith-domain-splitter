package com.monolith.splitter

import com.monolith.splitter.DomainTags.DOMAIN
import com.monolith.splitter.DomainTags.TEAM
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DomainMdcProvider(private val mdcProvider: MdcProvider) {
    fun putDomainTags(domainValue: DomainValue) {
        mdcProvider.put(DOMAIN, domainValue.lowercaseName())
        mdcProvider.put(TEAM, domainValue.team.lowercaseName())
    }

    fun findCurrentDomainTag(): DomainValue? =
        mdcProvider.get(DOMAIN)?.let { domainName ->
            DomainRegistry.fromString(domainName).also { matched ->
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
        val log = LoggerFactory.getLogger(DomainMdcProvider::class.java)
    }
}