package com.konsus.domaintag

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

internal class DomainMethodInterceptor(
    private val domainTagsService: DomainTagsService,
    private val domainProvider: DomainProvider,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        val domain = domainProvider.findInAopCall(invocation)
            ?: return invocation.proceed()
        val domainValue = domain.value
            .java
            .enumConstants
            .first()
        return domainTagsService.invoke(domainValue, invocation::proceed)
    }
}
