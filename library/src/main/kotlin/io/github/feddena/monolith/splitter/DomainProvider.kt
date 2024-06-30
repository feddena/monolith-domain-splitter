package io.github.feddena.monolith.splitter

import org.aopalliance.intercept.MethodInvocation
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method

@Component
class DomainProvider {
    fun findInAopCall(targetMethod: MethodInvocation): io.github.feddena.monolith.splitter.Domain? =
        getDomainFromMethodOrParentBean(targetMethod.method, targetMethod.getThis())

    fun findInMvcCall(targetMethod: HandlerMethod): io.github.feddena.monolith.splitter.Domain? =
        getDomainFromMethodOrParentBean(targetMethod.method, targetMethod.bean)

    private fun getDomainFromMethodOrParentBean(
        method: Method,
        bean: Any?,
    ): io.github.feddena.monolith.splitter.Domain? {
        return method.getAnnotation(io.github.feddena.monolith.splitter.Domain::class.java)
            ?: bean.let { AopProxyUtils.ultimateTargetClass(bean!!)?.getAnnotation(io.github.feddena.monolith.splitter.Domain::class.java) }
    }
}
