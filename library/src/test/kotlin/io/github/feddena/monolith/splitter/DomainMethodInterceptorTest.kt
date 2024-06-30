package io.github.feddena.monolith.splitter

import io.github.feddena.monolith.splitter.utils.DomainValueImpl
import io.github.feddena.monolith.splitter.utils.FakeSpan
import org.aopalliance.intercept.MethodInvocation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.slf4j.MDC
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Method

@ExtendWith(SoftAssertionsExtension::class)
@ExtendWith(OutputCaptureExtension::class)
class DomainMethodInterceptorTest {
    @InjectSoftAssertions
    private lateinit var softly: SoftAssertions

    private val spanProvider = mock<SpanProvider> {}
    private val domainSpanService = DomainSpanService(spanProvider)

    private val mdcProvider = MdcProvider()
    private val domainRegistry = DomainRegistry()
    private val domainMdcProvider = DomainMdcProvider(mdcProvider, domainRegistry)

    private val domainTagsService = DomainTagsService(domainSpanService, domainMdcProvider)
    private val domainProvider = DomainProvider()

    private val span = FakeSpan()

    private val interceptor: DomainMethodInterceptor =
        DomainMethodInterceptor(
            domainTagsService,
            domainProvider,
            domainRegistry,
        )

    @BeforeEach
    fun setup() {
        whenever(spanProvider.activeSpan()).thenReturn(span)
        MDC.clear()
        DomainValueImpl.entries
            .forEach {
                domainRegistry.registerDomainValue(it)
            }
    }

    /** Libraries change, let's see this still works */
    @Test
    fun checkHowMdcWorks() {
        MDC.put("k1", "v1")
        MDC.put("k1", "v2")
        assertThat(MDC.get("k1")).isEqualTo("v2")
        assertThat(MDC.get("k5672")).isNull()
        assertThat(MDC.getCopyOfContextMap()).containsOnlyKeys("k1")
        MDC.clear()
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
    }

    @Test
    fun `should set tag value from method's annotation`() {
        // given
        val invocation = FakeInvocation(ExampleEndpoint(), "testMethodWithDomain")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(invocation.recordedDomain).isEqualTo("assets")
        softly.assertThat(invocation.recordedTeam).isEqualTo("snails")
        softly.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
        softly.assertThat(span.getTag("domain")).isEqualTo("assets")
    }

    @Test
    fun `should not set any tag if neither method nor class has annotation`() {
        // given
        val invocation = FakeInvocation(ExampleEndpoint(), "testMethodWithoutDomain")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(invocation.recordedDomain).isNull()
        softly.assertThat(invocation.recordedTeam).isNull()
        softly.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
        softly.assertThat(span.tags).isEmpty()
    }

    @Test
    fun `should set tag value from class's annotation if method does not have it`() {
        // given
        val invocation = FakeInvocation(ExampleListenerWithDomain(), "testMethodWithoutDomain")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(invocation.recordedDomain).isEqualTo("assets")
        softly.assertThat(invocation.recordedTeam).isEqualTo("snails")
        softly.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
        softly.assertThat(span.getTag("domain")).isEqualTo("assets")
    }

    @Test
    fun `should set tag value from method's annotation if both method and class have it`() {
        // given
        val invocation = FakeInvocation(ExampleListenerWithDomain(), "testMethodWithDomain")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(invocation.recordedDomain).isEqualTo("project")
        softly.assertThat(invocation.recordedTeam).isEqualTo("lions")
        softly.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
        softly.assertThat(span.getTag("domain")).isEqualTo("project")
    }

    @Test
    fun `should clean MDC when MDC before invocation contained wrong data`(loggerOutput: CapturedOutput) {
        // given
        val invocation = FakeInvocation(ExampleListenerWithDomain(), "testMethodWithoutDomain")
        MDC.put("domain", "no-such-domain")
        MDC.put("team", "coordination")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
        softly.assertThat(loggerOutput.out).contains("Found incorrect domain in MDC", "no-such-domain")
    }

    @Test
    fun `should not change domain when invocation has no domain set`(loggerOutput: CapturedOutput) {
        // given
        val invocation = FakeInvocation(ExampleEndpoint(), "testMethodWithoutDomain")
        MDC.put("domain", "company")
        MDC.put("team", "coordination")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(MDC.getCopyOfContextMap()).containsExactly(entry("domain", "company"), entry("team", "coordination"))
        softly.assertThat(loggerOutput.out).doesNotContain("Found incorrect domain in MDC")
    }

    @Test
    fun `should restore MDC when invocation has domain set`(loggerOutput: CapturedOutput) {
        // given
        val invocation = FakeInvocation(ExampleListenerWithDomain(), "testMethodWithoutDomain")
        MDC.put("domain", "project")
        MDC.put("team", "lions")
        // when
        interceptor.invoke(invocation)
        // then
        softly.assertThat(invocation.invocationCount).isEqualTo(1)
        softly.assertThat(MDC.getCopyOfContextMap()).containsExactly(entry("domain", "project"), entry("team", "lions"))
        softly.assertThat(loggerOutput.out).doesNotContain("Found incorrect domain in MDC")
    }

    private class ExampleEndpoint {
        @io.github.feddena.monolith.splitter.Domain("ASSETS")
        fun testMethodWithDomain() {
        }

        fun testMethodWithoutDomain() {}
    }

    @io.github.feddena.monolith.splitter.Domain("ASSETS")
    private class ExampleListenerWithDomain {
        @io.github.feddena.monolith.splitter.Domain("PROJECT")
        fun testMethodWithDomain() {
        }

        fun testMethodWithoutDomain() {}
    }

    private class FakeInvocation(private val target: Any, methodName: String) : MethodInvocation {
        private val method: Method = target::class.java.getMethod(methodName)

        var recordedDomain: String? = null
            private set
        var recordedTeam: String? = null
            private set
        var invocationCount = 0
            private set

        override fun proceed() {
            recordedDomain = MDC.get("domain")
            recordedTeam = MDC.get("team")
            ++invocationCount
        }

        override fun getThis() = target

        override fun getMethod() = method

        override fun getArguments(): Array<Any> = emptyArray()

        override fun getStaticPart(): AccessibleObject {
            TODO("Not yet implemented")
        }
    }
}
