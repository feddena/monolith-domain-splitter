package io.github.feddena.monolith.splitter

import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class MdcProvider {
    fun get(key: String): String? = MDC.get(key)

    fun put(
        key: String,
        value: String?,
    ) = MDC.put(key, value)

    fun remove(key: String) = MDC.remove(key)
}
