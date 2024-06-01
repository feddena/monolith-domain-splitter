package com.konsus.domaintag

import org.aopalliance.intercept.MethodInvocation
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method

@Component
class DomainProvider {
    fun findInAopCall(targetMethod: MethodInvocation): Domain? =
        getDomainFromMethodOrParentBean(targetMethod.method, targetMethod.getThis())

    fun findInMvcCall(targetMethod: HandlerMethod): Domain? =
        getDomainFromMethodOrParentBean(targetMethod.method, targetMethod.bean)

    private fun getDomainFromMethodOrParentBean(method: Method, bean: Any?): Domain? {
        return method.getAnnotation(Domain::class.java)
            ?: bean.let { AopProxyUtils.ultimateTargetClass(bean!!)?.getAnnotation(Domain::class.java) }
    }
}
