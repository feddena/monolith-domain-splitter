package io.github.feddena.monolith.splitter

interface DomainValue {
    val team: Team
    val name: String

    fun lowercaseName(): String = name.lowercase()
}

interface Team {
    val name: String

    fun lowercaseName(): String = name.lowercase()
}
