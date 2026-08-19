package com.gms.cheerlotandroid.data.source

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

private enum class TeamCode(val id: String) {
    DOOSAN(id = "DOOSAN"),
    HANWHA(id = "HANWHA"),
    KIA(id = "KIA"),
    KIWOOM(id = "KIWOOM"),
    KT(id = "KT"),
    LG(id = "LG"),
    LOTTE(id = "LOTTE"),
    NC(id = "NC"),
    SAMSUNG(id = "SAMSUNG"),
    SSG(id = "SSG");

    companion object {
        fun fromId(teamId: String): TeamCode? {
            val normalizedId = teamId.trim().uppercase()
            return entries.firstOrNull { it.id == normalizedId }
        }
    }
}

private enum class ApiCode(val value: String) {
    DOOSAN(value = "ob"),
    HANWHA(value = "hh"),
    KIA(value = "ht"),
    KIWOOM(value = "wo"),
    KT(value = "kt"),
    LG(value = "lg"),
    LOTTE(value = "lt"),
    NC(value = "nc"),
    SAMSUNG(value = "ss"),
    SSG(value = "sk");

    companion object {
        fun fromValue(apiCode: String): ApiCode? {
            val normalizedApiCode = apiCode.trim().lowercase()
            return entries.firstOrNull { it.value == normalizedApiCode }
        }
    }
}

object TeamCatalog {
    val teams: List<TeamInfo> = TeamCode.entries.map { it.toTeamInfo() }

    fun findById(teamId: TeamId): TeamInfo? {
        val code = TeamCode.fromId(teamId.value) ?: return null
        return code.toTeamInfo()
    }

    fun findByApiCode(apiCode: String): TeamInfo? {
        val code = fromApiCode(apiCode) ?: return null
        return code.toTeamInfo()
    }

    fun toApiCode(teamId: TeamId): String {
        val code = TeamCode.fromId(teamId.value)
            ?: error("Unknown TeamId: ${teamId.value}")
        return code.toApiCode().value
    }

    private fun fromApiCode(apiCode: String): TeamCode? {
        return ApiCode.fromValue(apiCode)?.toTeamCode()
    }
}

private fun TeamCode.toTeamInfo(): TeamInfo {
    return when (this) {
        TeamCode.SAMSUNG -> TeamInfo(
            id = TeamId(id),
            shortName = "삼성",
            longName = "삼성 라이온즈",
            englishFullName = "SAMSUNG LIONS",
            slogan = "WIN or WOW!"
        )

        TeamCode.HANWHA -> TeamInfo(
            id = TeamId(id),
            shortName = "한화",
            longName = "한화 이글스",
            englishFullName = "HANWHA EAGLES",
            slogan = "IT IS OUR TURN"
        )

        TeamCode.LG -> TeamInfo(
            id = TeamId(id),
            shortName = "LG",
            longName = "LG 트윈스",
            englishFullName = "LG TWINS",
            slogan = "무적 LG! 끝까지 TWINS!"
        )

        TeamCode.LOTTE -> TeamInfo(
            id = TeamId(id),
            shortName = "롯데",
            longName = "롯데 자이언츠",
            englishFullName = "LOTTE GIANTS",
            slogan = "투혼투지, GO HIGH"
        )

        TeamCode.NC -> TeamInfo(
            id = TeamId(id),
            shortName = "NC",
            longName = "NC 다이노스",
            englishFullName = "NC DINOS",
            slogan = "거침없이 가자! 위풍당당"
        )

        TeamCode.SSG -> TeamInfo(
            id = TeamId(id),
            shortName = "SSG",
            longName = "SSG 랜더스",
            englishFullName = "SSG LANDERS",
            slogan = "NO LIMITS, AMAZING LANDERS"
        )

        TeamCode.DOOSAN -> TeamInfo(
            id = TeamId(id),
            shortName = "두산",
            longName = "두산 베어스",
            englishFullName = "DOOSAN BEARS",
            slogan = "TIME TO MOVE ON"
        )

        TeamCode.KT -> TeamInfo(
            id = TeamId(id),
            shortName = "KT",
            longName = "KT 위즈",
            englishFullName = "KT WIZ",
            slogan = "마법의 시작, 위대한 도약! GREAT KT"
        )

        TeamCode.KIWOOM -> TeamInfo(
            id = TeamId(id),
            shortName = "키움",
            longName = "키움 히어로즈",
            englishFullName = "KIWOOM HEROES",
            slogan = "영웅, 도전, 승리"
        )

        TeamCode.KIA -> TeamInfo(
            id = TeamId(id),
            shortName = "KIA",
            longName = "기아 타이거즈",
            englishFullName = "KIA TIGERS",
            slogan = "다시, 뜨겁게 ALWAYS KIA TIGERS"
        )
    }
}

private fun TeamCode.toApiCode(): ApiCode {
    return when (this) {
        TeamCode.DOOSAN -> ApiCode.DOOSAN
        TeamCode.HANWHA -> ApiCode.HANWHA
        TeamCode.KIA -> ApiCode.KIA
        TeamCode.KIWOOM -> ApiCode.KIWOOM
        TeamCode.KT -> ApiCode.KT
        TeamCode.LG -> ApiCode.LG
        TeamCode.LOTTE -> ApiCode.LOTTE
        TeamCode.NC -> ApiCode.NC
        TeamCode.SAMSUNG -> ApiCode.SAMSUNG
        TeamCode.SSG -> ApiCode.SSG
    }
}

private fun ApiCode.toTeamCode(): TeamCode {
    return when (this) {
        ApiCode.DOOSAN -> TeamCode.DOOSAN
        ApiCode.HANWHA -> TeamCode.HANWHA
        ApiCode.KIA -> TeamCode.KIA
        ApiCode.KIWOOM -> TeamCode.KIWOOM
        ApiCode.KT -> TeamCode.KT
        ApiCode.LG -> TeamCode.LG
        ApiCode.LOTTE -> TeamCode.LOTTE
        ApiCode.NC -> TeamCode.NC
        ApiCode.SAMSUNG -> TeamCode.SAMSUNG
        ApiCode.SSG -> TeamCode.SSG
    }
}
