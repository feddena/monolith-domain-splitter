package com.monolith.splitter

enum class DomainValueImpl(
    override val team: Team,
) : DomainValue {
    PROJECT(TeamImpl.LIONS),
    CHAT(TeamImpl.SQUIRRELS),
}

enum class TeamImpl : Team {
    LIONS,
    SQUIRRELS,
}
