package com.monolith.splitter

import com.monolith.splitter.utils.DomainTraceInterceptorConfigurationStub
import com.monolith.splitter.utils.DomainValueImpl
import datadog.trace.api.interceptor.MutableSpan
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SoftAssertionsExtension::class)
class DomainTraceInterceptorTest {
    @InjectSoftAssertions
    private lateinit var softly: SoftAssertions

    private val domainRegistry = DomainRegistry()
    private val domainTraceInterceptorConfiguration = DomainTraceInterceptorConfigurationStub()
    private lateinit var interceptor: DomainTraceInterceptor

    @BeforeEach
    fun setup() {
        DomainValueImpl.entries
            .forEach { domainRegistry.registerDomainValue(it) }
        interceptor = DomainTraceInterceptor(domainTraceInterceptorConfiguration, domainRegistry)
    }

    @Test
    fun `should set the domain tag for all spans when there is only one domain`() {
        val span1 = FakeMutableSpan(tags = mapOf("domain" to "assets"))
        val span2 = FakeMutableSpan()

        interceptor.onTraceComplete(mutableListOf(span1, span2))

        softly.assertThat(span1.getTag("domain")).isEqualTo("assets")
        softly.assertThat(span1.getTag("team")).isEqualTo("snails")
        softly.assertThat(span2.getTag("domain")).isEqualTo("assets")
        softly.assertThat(span2.getTag("team")).isEqualTo("snails")
    }

    @Test
    fun `should set the domain tag of the earliest span for all spans when there are multiple domain tags`() {
        val span1 = FakeMutableSpan(tags = mapOf("domain" to "files"), startTime = 3)
        val span2 = FakeMutableSpan(tags = mapOf("domain" to "assets"), startTime = 2)
        val span3 = FakeMutableSpan(startTime = 1)

        interceptor.onTraceComplete(mutableListOf(span1, span2, span3))

        softly.assertThat(span1.getTag("domain")).isEqualTo("assets")
        softly.assertThat(span1.getTag("team")).isEqualTo("snails")
        softly.assertThat(span2.getTag("domain")).isEqualTo("assets")
        softly.assertThat(span2.getTag("team")).isEqualTo("snails")
        softly.assertThat(span3.getTag("domain")).isEqualTo("assets")
        softly.assertThat(span3.getTag("team")).isEqualTo("snails")
    }

    @Test
    fun `should set the service name for all spans when domain is enabled for service split`() {
        val span1 = FakeMutableSpan(tags = mapOf("domain" to "files"), service = "real-service-to-override")
        val span2 = FakeMutableSpan(service = "real-service-to-override")
        val span3 = FakeMutableSpan(service = "redis")

        interceptor.onTraceComplete(mutableListOf(span1, span2, span3))

        softly.assertThat(span1.serviceName).isEqualTo("splitter-files")
        softly.assertThat(span2.serviceName).isEqualTo("splitter-files")
        softly.assertThat(span3.serviceName).isEqualTo("redis")
    }

    @Test
    fun `should use parent span domain tags if current trace spans don't have them`() {
        val span1 = FakeMutableSpan(tags = mapOf("domain" to "files"))
        val span2 = FakeMutableSpan(parentSpan = span1)
        val span3 = FakeMutableSpan(parentSpan = span2)

        interceptor.onTraceComplete(mutableListOf(span2, span3))

        softly.assertThat(span2.serviceName).isEqualTo("splitter-files")
        softly.assertThat(span3.serviceName).isEqualTo("splitter-files")
    }

    private class FakeMutableSpan(
        private var service: String = "real-service-to-override",
        tags: Map<String, Any> = mapOf(),
        private val startTime: Long = 0,
        private val parentSpan: MutableSpan? = null,
    ) : MutableSpan {
        private val _tags = tags.toMutableMap()
        override fun getServiceName(): String = service

        override fun setServiceName(serviceName: String): MutableSpan {
            service = serviceName
            return this
        }

        override fun getTags(): MutableMap<String, Any> = _tags

        override fun setTag(tag: String, value: String): MutableSpan {
            _tags[tag] = value
            return this
        }

        override fun getStartTime(): Long = startTime

        override fun getLocalRootSpan(): MutableSpan = parentSpan ?: this

        override fun getOperationName(): CharSequence = "operation.name"

        override fun getResourceName(): CharSequence = "resource.name"

        override fun getSpanType(): String = "span.type"

        override fun getDurationNano(): Long = TODO("Not yet implemented")

        override fun setOperationName(serviceName: CharSequence?): MutableSpan = TODO("Not yet implemented")

        override fun setResourceName(resourceName: CharSequence?): MutableSpan = TODO("Not yet implemented")

        override fun getSamplingPriority(): Int = TODO("Not yet implemented")

        override fun setSamplingPriority(newPriority: Int): MutableSpan = TODO("Not yet implemented")

        override fun setSpanType(type: CharSequence?): MutableSpan = TODO("Not yet implemented")

        override fun setTag(tag: String?, value: Boolean): MutableSpan = TODO("Not yet implemented")

        override fun setTag(tag: String?, value: Number?): MutableSpan = TODO("Not yet implemented")

        override fun setMetric(metric: CharSequence?, value: Int): MutableSpan = TODO("Not yet implemented")

        override fun setMetric(metric: CharSequence?, value: Long): MutableSpan = TODO("Not yet implemented")

        override fun setMetric(metric: CharSequence?, value: Double): MutableSpan = TODO("Not yet implemented")

        override fun isError(): Boolean = TODO("Not yet implemented")

        override fun setError(value: Boolean): MutableSpan = TODO("Not yet implemented")

        override fun getRootSpan(): MutableSpan = TODO("Not yet implemented")
    }
}
