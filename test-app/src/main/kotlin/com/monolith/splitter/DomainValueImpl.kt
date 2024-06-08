package com.monolith.splitter

import com.monolith.splitter.DomainValue
import com.monolith.splitter.Team

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