package com.monolith.splitter

import com.monolith.splitter.utils.DomainValueImpl
import org.aopalliance.intercept.MethodInvocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal class DomainProviderTest {
    private val domainProvider = DomainProvider()

    @Nested
    inner class FindInMethodInvocation {
        @Test
        fun `returns Domain annotation from method`() {
            val methodInvocation = methodInvocationMethod(
                ExampleBeanWithDomain::class, "exampleMethodWithDomainAnnotation"
            )

            assertThat(domainProvider.findInAopCall(methodInvocation)).isEqualTo(Domain("CHAT"))
        }

        @Test
        fun `returns Domain annotation from bean if method has no annotation`() {
            val methodInvocation = methodInvocationMethod(
                ExampleBeanWithDomain::class, "exampleMethodWithoutDomainAnnotation"
            )

            assertThat(domainProvider.findInAopCall(methodInvocation)).isEqualTo(Domain("PROJECT"))
        }

        @Test
        fun `returns null if neither method nor bean have Domain annotation`() {
            //given
            val methodInvocation = methodInvocationMethod(
                ExampleBeanWithoutDomain::class, "exampleMethodWithoutDomainAnnotation"
            )

            assertThat(domainProvider.findInAopCall(methodInvocation)).isNull()
        }

        private fun methodInvocationMethod(beanClass: KClass<*>, methodName: String): MethodInvocation {
            val methodInvocation: MethodInvocation = mock()
            val method: Method = beanClass.java.getMethod(methodName)
            whenever(methodInvocation.method).thenReturn(method)
            whenever(methodInvocation.getThis()).thenReturn(beanClass.java.getDeclaredConstructor().newInstance())
            return methodInvocation
        }
    }

    @Nested
    inner class FindInHandlerMethod {
        @Test
        fun `returns Domain annotation from method`() {
            val handlerMethod = handlerMethod(
                ExampleBeanWithDomain::class, "exampleMethodWithDomainAnnotation"
            )

            assertThat(domainProvider.findInMvcCall(handlerMethod)).isEqualTo(Domain("CHAT"))
        }

        @Test
        fun `returns Domain annotation from bean if method has no annotation`() {
            val handlerMethod = handlerMethod(
                ExampleBeanWithDomain::class, "exampleMethodWithoutDomainAnnotation"
            )

            assertThat(domainProvider.findInMvcCall(handlerMethod)).isEqualTo(Domain("PROJECT"))
        }

        @Test
        fun `returns null if neither method nor bean have Domain annotation`() {
            val handlerMethod = handlerMethod(
                ExampleBeanWithoutDomain::class, "exampleMethodWithoutDomainAnnotation"
            )

            assertThat(domainProvider.findInMvcCall(handlerMethod)).isNull()
        }

        private fun handlerMethod(beanClass: KClass<*>, methodName: String): HandlerMethod {
            val handlerMethod: HandlerMethod = mock()
            val method: Method = beanClass.java.getMethod(methodName)
            whenever(handlerMethod.method).thenReturn(method)
            whenever(handlerMethod.bean).thenReturn(beanClass.java.getDeclaredConstructor().newInstance())
            return handlerMethod
        }
    }
}

@Domain("PROJECT")
private class ExampleBeanWithDomain {
    @Domain("CHAT")
    fun exampleMethodWithDomainAnnotation() {}
    fun exampleMethodWithoutDomainAnnotation() {}
}

private class ExampleBeanWithoutDomain {
    fun exampleMethodWithoutDomainAnnotation() {}
}
