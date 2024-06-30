package io.github.feddena.monolith.splitter

import org.springframework.stereotype.Service

@Service
class DomainTagsService(
    private val domainSpanService: DomainSpanService,
    private val domainMdcProvider: DomainMdcProvider,
) {
    fun <T> invoke(
        domain: DomainValue?,
        invocation: () -> T,
    ): T {
        if (domain == null) {
            return invocation()
        }

        domainSpanService.addDomainTagToSpan(domain)

        val mdcStateToRestore = domainMdcProvider.findCurrentDomainTag()
        try {
            domainMdcProvider.putDomainTags(domain)
            return invocation()
        } finally {
            if (mdcStateToRestore != null) {
                domainMdcProvider.putDomainTags(mdcStateToRestore)
            } else {
                domainMdcProvider.clearDomainTags()
            }
        }
    }
}
