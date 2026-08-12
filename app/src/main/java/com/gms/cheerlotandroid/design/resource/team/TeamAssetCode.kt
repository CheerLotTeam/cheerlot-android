package com.gms.cheerlotandroid.design.resource.team

import com.gms.cheerlotandroid.domain.model.team.TeamId

// Figma와 iOS, Android 팀별 디자인 에셋이 공통으로 사용하는 코드입니다.
enum class TeamAssetCode(val value: String) {
    HANWHA("hh"),
    LG("lg"),
    LOTTE("lt"),
    SAMSUNG("ss"),
    NC("nc"),
    KT("kt"),
    SSG("ssg"),
    DOOSAN("ds"),
    KIWOOM("kw"),
    KIA("kia");

    companion object {
        fun fromOrNull(teamId: TeamId): TeamAssetCode? {
            val normalizedTeamId = teamId.value.trim().uppercase()
            return entries.firstOrNull { it.name == normalizedTeamId }
        }

        fun from(teamId: TeamId): TeamAssetCode {
            return fromOrNull(teamId) ?: error("Unknown TeamId: ${teamId.value}")
        }
    }
}
