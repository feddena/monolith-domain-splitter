package com.monolith.splitter

import com.monolith.splitter.DomainTags.DOMAIN
import com.monolith.splitter.DomainTags.TEAM
import com.monolith.splitter.configuration.DomainTraceInterceptorConfiguration
import datadog.trace.api.interceptor.MutableSpan
import datadog.trace.api.interceptor.TraceInterceptor
import org.springframework.stereotype.Component

@Component
internal class DomainTraceInterceptor(
    private val settings: DomainTraceInterceptorConfiguration
) : TraceInterceptor {
    override fun onTraceComplete(spans: MutableCollection<out MutableSpan>): MutableCollection<out MutableSpan> {
        findClosestDomainInSpans(spans)?.also { earliestDomain ->
            spans.forEach {
                if (shouldOverrideDDService(it.serviceName)) {
                    it.serviceName = settings.getServiceNamePrefix() + earliestDomain.lowercaseName()
                }
                it.setTag(DOMAIN, earliestDomain.lowercaseName())
                it.setTag(TEAM, earliestDomain.team.lowercaseName())
            }
        }

        return spans
    }

    override fun priority() = 30

    /**
     * In environments with very long traces, sometimes the trace spans can exceed typical limits (e.g., 1000 spans).
     * When this occurs, not all spans may be present in the collection due to these limits.
     * To ensure domain tags are captured, this interceptor fetches domain tags from parent spans within the current trace.
     */
    private fun findClosestDomainInSpans(spans: Collection<MutableSpan>): DomainValue? {
        val closestSpanWithDomain = findEarliestDomainInTraceSpans(spans) ?: findLatestDomainInParentSpans(spans)
        return closestSpanWithDomain?.tags?.get(DOMAIN)?.let { DomainRegistry.fromString(it as String)!! }
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

    private fun shouldOverrideDDService(serviceName: String?) =
        serviceName in settings.getServicesToOverride()
}