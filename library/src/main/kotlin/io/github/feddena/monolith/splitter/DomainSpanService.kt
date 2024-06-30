package io.github.feddena.monolith.splitter

import io.github.feddena.monolith.splitter.DomainTags.DOMAIN
import io.opentracing.Span
import io.opentracing.util.GlobalTracer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class DomainSpanService(private val spanProvider: SpanProvider) {
    fun addDomainTagToSpan(domain: DomainValue) {
        val span = spanProvider.activeSpan()
        span.setTag(DOMAIN, domain.lowercaseName())
    }
}

@Component
class SpanProvider {
    fun activeSpan(): Span = GlobalTracer.get().activeSpan()
}
