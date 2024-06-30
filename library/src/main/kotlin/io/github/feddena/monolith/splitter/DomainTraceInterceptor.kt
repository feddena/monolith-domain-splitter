package io.github.feddena.monolith.splitter

import datadog.trace.api.interceptor.MutableSpan
import datadog.trace.api.interceptor.TraceInterceptor
import io.github.feddena.monolith.splitter.DomainTags.DOMAIN
import io.github.feddena.monolith.splitter.DomainTags.TEAM
import io.github.feddena.monolith.splitter.configuration.DomainTraceInterceptorConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DomainTraceInterceptor(
    private val settings: DomainTraceInterceptorConfiguration,
    private val domainRegistry: DomainRegistry,
) : TraceInterceptor {
    override fun priority() = 30

    override fun onTraceComplete(spans: MutableCollection<out MutableSpan>): MutableCollection<out MutableSpan> {
        findClosestDomainInSpans(spans)?.also { earliestDomain ->
            val domain = earliestDomain.lowercaseName()
            val team = earliestDomain.team.lowercaseName()
            spans.forEach {
                if (shouldOverrideDDService(it.serviceName)) {
                    it.serviceName = settings.getServiceNamePrefix() + domain
                }
                it.setTag(DOMAIN, domain)
                it.setTag(TEAM, team)
            }
            log.info(
                "Successfully set domain and team to spans" +
                    " - domain=$domain" +
                    ", team=$team" +
                    ", serviceName=${spans.firstOrNull()?.serviceName}",
            )
        }
        return spans
    }

    /**
     * In environments with very long traces, sometimes the trace spans can exceed typical limits (e.g., 1000 spans).
     * When this occurs, not all spans may be present in the collection due to these limits.
     * To ensure domain tags are captured, this interceptor fetches domain tags from parent spans within the current trace.
     */
    private fun findClosestDomainInSpans(spans: Collection<MutableSpan>): DomainValue? {
        val closestSpanWithDomain = findEarliestDomainInTraceSpans(spans) ?: findLatestDomainInParentSpans(spans)
        return closestSpanWithDomain?.tags
            ?.get(DOMAIN)
            ?.let { domainRegistry.fromString(it as String) }
    }

    private fun findEarliestDomainInTraceSpans(spans: Collection<MutableSpan>): MutableSpan? =
        spans
            .filter { it.tags.containsKey(DOMAIN) }
            .minByOrNull { it.startTime }

    private fun findLatestDomainInParentSpans(spans: Collection<MutableSpan>): MutableSpan? =
        spans
            .mapNotNull { it.localRootSpan }
            .filter { it.tags.containsKey(DOMAIN) }
            .maxByOrNull { it.startTime }

    private fun shouldOverrideDDService(serviceName: String?) = serviceName in settings.getServicesToOverride()

    private companion object {
        private val log = LoggerFactory.getLogger(DomainTraceInterceptor::class.java)
    }
}
