package com.monolith.splitter

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class DomainHandlerInterceptor(
    private val domainMdcProvider: DomainMdcProvider,
    private val domainSpanService: DomainSpanService,
    private val domainProvider: DomainProvider,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        putDomainTagsInSpanAndMdc(handler)
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        domainMdcProvider.clearDomainTags()
    }

    private fun putDomainTagsInSpanAndMdc(handler: Any) {
        if (handler is HandlerMethod) {
            domainProvider.findInMvcCall(handler)?.also { domain ->
                val domainValue = domain.value
                    .java
                    .enumConstants
                    .first()
                domainSpanService.addDomainTagToSpan(domainValue)
                domainMdcProvider.putDomainTags(domainValue)
            }
        } else {
            log.warn("Failed to enrich domain tag for handler ${handler.javaClass}")
        }
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(DomainHandlerInterceptor::class.java)
    }
}
