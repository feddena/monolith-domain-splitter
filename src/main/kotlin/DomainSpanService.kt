package com.konsus.domaintag

import com.konsus.domaintag.DomainTags.DOMAIN
import io.opentracing.util.GlobalTracer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class DomainSpanService(private val spanProvider: SpanProvider) {
    fun addDomainTagToSpan(domain: DomainValue) {
        val span = spanProvider.activeSpan()
        span?.setTag(DOMAIN, domain.lowercaseName())
    }
}

@Component
class SpanProvider {
    fun activeSpan() = GlobalTracer.get().activeSpan()
}
