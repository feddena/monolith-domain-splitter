package com.monolith.splitter

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.LoggerFactory

internal class DomainMethodInterceptor(
    private val domainTagsService: DomainTagsService,
    private val domainProvider: DomainProvider,
    private val domainRegistry: DomainRegistry,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        LoggerFactory.getLogger(DomainMethodInterceptor::class.java).info("Method with domain tag invoked") // TODO
        val domain = domainProvider.findInAopCall(invocation)
            ?: return invocation.proceed()
        val domainValue = domainRegistry.fromString(domain.value)
        return domainTagsService.invoke(domainValue, invocation::proceed)
    }
}
