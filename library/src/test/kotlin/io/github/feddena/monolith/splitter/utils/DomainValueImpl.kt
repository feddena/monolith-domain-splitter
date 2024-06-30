package io.github.feddena.monolith.splitter.utils

import io.github.feddena.monolith.splitter.DomainValue
import io.github.feddena.monolith.splitter.Team

enum class DomainValueImpl(
    override val team: Team,
) : DomainValue {
    PROJECT(TeamImpl.LIONS),
    CHAT(TeamImpl.SQUIRRELS),
    ASSETS(TeamImpl.SNAILS),
    FILES(TeamImpl.SNAILS),
}

enum class TeamImpl : Team {
    LIONS,
    SQUIRRELS,
    SNAILS,
}
