package io.github.feddena.monolith.splitter

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

internal class DomainMethodInterceptor(
    private val domainTagsService: DomainTagsService,
    private val domainProvider: DomainProvider,
    private val domainRegistry: DomainRegistry,
) : MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        val domain =
            domainProvider.findInAopCall(invocation)
                ?: return invocation.proceed()
        val domainValue = domainRegistry.fromString(domain.value)
        return domainTagsService.invoke(domainValue, invocation::proceed)
    }
}
