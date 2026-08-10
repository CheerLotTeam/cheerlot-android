package com.gms.cheerlotandroid.design.color.team

import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.domain.model.team.TeamId

// TeamColor: 팀 ID를 색상으로 해석하는 resolver
/** 팀 primary 팔레트입니다. `color400`은 대표색으로 `TeamColors.primary`에 둡니다. */
data class TeamPrimaryPalette(
    val color100: Color,
    val color200: Color,
    val color300: Color,
    val color500: Color,
    val color600: Color
)

/**
 * 팀 secondary 팔레트입니다.
 *
 * `color400`은 팀 대표 secondary 색상으로 별도 노출하기 때문에 팔레트에서는 제외합니다.
 */
data class TeamSecondaryPalette(
    val color100: Color,
    val color200: Color,
    val color300: Color,
    val color500: Color,
    val color600: Color
)

/** `TeamId`에 대응하는 대표 색상과 primary/secondary 팔레트를 반환합니다. */
object TeamColor {
    // 팀별 대표 primary/secondary 색상
    val HanwhaPrimary = TeamPaletteColor.HhOrange400
    val HanwhaSecondary = TeamPaletteColor.HhOrange400

    val LgPrimary = TeamPaletteColor.LgRed400
    val LgSecondary = TeamPaletteColor.LgRed400

    val LottePrimary = TeamPaletteColor.LtNavy400
    val LotteSecondary = TeamPaletteColor.LtRed400

    val SamsungPrimary = TeamPaletteColor.SsBlue400
    val SamsungSecondary = TeamPaletteColor.SsBlue400

    val NcPrimary = TeamPaletteColor.NcDeepblue400
    val NcSecondary = TeamPaletteColor.NcGold400

    val KtPrimary = TeamPaletteColor.KtJetblack400
    val KtSecondary = TeamPaletteColor.KtRed400

    val SsgPrimary = TeamPaletteColor.SsgDeepred400
    val SsgSecondary = TeamPaletteColor.SsgDeepred400

    val DoosanPrimary = TeamPaletteColor.DsMidnight400
    val DoosanSecondary = TeamPaletteColor.DsRed400

    val KiwoomPrimary = TeamPaletteColor.KwBurgundy400
    val KiwoomSecondary = TeamPaletteColor.KwBurgundy400

    val KiaPrimary = TeamPaletteColor.KiaScarlet400
    val KiaSecondary = TeamPaletteColor.KiaScarlet400

    fun colorsFor(teamId: TeamId): TeamColors {
        val assetPrefix = assetPrefixFor(teamId)
        val (primary, secondary) = when (assetPrefix) {
            "hh" -> HanwhaPrimary to HanwhaSecondary
            "lg" -> LgPrimary to LgSecondary
            "lt" -> LottePrimary to LotteSecondary
            "ss" -> SamsungPrimary to SamsungSecondary
            "nc" -> NcPrimary to NcSecondary
            "kt" -> KtPrimary to KtSecondary
            "ssg" -> SsgPrimary to SsgSecondary
            "ds" -> DoosanPrimary to DoosanSecondary
            "kw" -> KiwoomPrimary to KiwoomSecondary
            "kia" -> KiaPrimary to KiaSecondary
            else -> error("Unknown team asset prefix: $assetPrefix")
        }

        return TeamColors(
            primary = primary,
            secondary = secondary,
            primaryPalette = primaryPaletteFor(assetPrefix),
            secondaryPalette = secondaryPaletteFor(assetPrefix)
        )
    }

    // 팀별 primary 팔레트
    private fun primaryPaletteFor(assetPrefix: String): TeamPrimaryPalette {
        return when (assetPrefix.lowercase()) {
            "hh" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.HhOrange100,
                color200 = TeamPaletteColor.HhOrange200,
                color300 = TeamPaletteColor.HhOrange300,
                color500 = TeamPaletteColor.HhOrange500,
                color600 = TeamPaletteColor.HhOrange600
            )
            "lg" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.LgRed100,
                color200 = TeamPaletteColor.LgRed200,
                color300 = TeamPaletteColor.LgRed300,
                color500 = TeamPaletteColor.LgRed500,
                color600 = TeamPaletteColor.LgRed600
            )
            "lt" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.LtNavy100,
                color200 = TeamPaletteColor.LtNavy200,
                color300 = TeamPaletteColor.LtNavy300,
                color500 = TeamPaletteColor.LtNavy500,
                color600 = TeamPaletteColor.LtNavy600
            )
            "ss" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.SsBlue100,
                color200 = TeamPaletteColor.SsBlue200,
                color300 = TeamPaletteColor.SsBlue300,
                color500 = TeamPaletteColor.SsBlue500,
                color600 = TeamPaletteColor.SsBlue600
            )
            "nc" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.NcDeepblue100,
                color200 = TeamPaletteColor.NcDeepblue200,
                color300 = TeamPaletteColor.NcDeepblue300,
                color500 = TeamPaletteColor.NcDeepblue500,
                color600 = TeamPaletteColor.NcDeepblue600
            )
            "kt" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.KtJetblack100,
                color200 = TeamPaletteColor.KtJetblack200,
                color300 = TeamPaletteColor.KtJetblack300,
                color500 = TeamPaletteColor.KtJetblack500,
                color600 = TeamPaletteColor.KtJetblack600
            )
            "ssg" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.SsgDeepred100,
                color200 = TeamPaletteColor.SsgDeepred200,
                color300 = TeamPaletteColor.SsgDeepred300,
                color500 = TeamPaletteColor.SsgDeepred500,
                color600 = TeamPaletteColor.SsgDeepred600
            )
            "ds" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.DsMidnight100,
                color200 = TeamPaletteColor.DsMidnight200,
                color300 = TeamPaletteColor.DsMidnight300,
                color500 = TeamPaletteColor.DsMidnight500,
                color600 = TeamPaletteColor.DsMidnight600
            )
            "kw" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.KwBurgundy100,
                color200 = TeamPaletteColor.KwBurgundy200,
                color300 = TeamPaletteColor.KwBurgundy300,
                color500 = TeamPaletteColor.KwBurgundy500,
                color600 = TeamPaletteColor.KwBurgundy600
            )
            "kia" -> TeamPrimaryPalette(
                color100 = TeamPaletteColor.KiaScarlet100,
                color200 = TeamPaletteColor.KiaScarlet200,
                color300 = TeamPaletteColor.KiaScarlet300,
                color500 = TeamPaletteColor.KiaScarlet500,
                color600 = TeamPaletteColor.KiaScarlet600
            )
            else -> error("Unknown team asset prefix: $assetPrefix")
        }
    }

    // 팀별 secondary 팔레트
    private fun secondaryPaletteFor(assetPrefix: String): TeamSecondaryPalette {
        return when (assetPrefix.lowercase()) {
            "hh" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.HhOrange100,
                color200 = TeamPaletteColor.HhOrange200,
                color300 = TeamPaletteColor.HhOrange300,
                color500 = TeamPaletteColor.HhOrange500,
                color600 = TeamPaletteColor.HhOrange600
            )
            "lg" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.LgRed100,
                color200 = TeamPaletteColor.LgRed200,
                color300 = TeamPaletteColor.LgRed300,
                color500 = TeamPaletteColor.LgRed500,
                color600 = TeamPaletteColor.LgRed600
            )
            "lt" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.LtRed100,
                color200 = TeamPaletteColor.LtRed200,
                color300 = TeamPaletteColor.LtRed300,
                color500 = TeamPaletteColor.LtRed500,
                color600 = TeamPaletteColor.LtRed600
            )
            "ss" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.SsBlue100,
                color200 = TeamPaletteColor.SsBlue200,
                color300 = TeamPaletteColor.SsBlue300,
                color500 = TeamPaletteColor.SsBlue500,
                color600 = TeamPaletteColor.SsBlue600
            )
            "nc" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.NcGold100,
                color200 = TeamPaletteColor.NcGold200,
                color300 = TeamPaletteColor.NcGold300,
                color500 = TeamPaletteColor.NcGold500,
                color600 = TeamPaletteColor.NcGold600
            )
            "kt" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.KtRed100,
                color200 = TeamPaletteColor.KtRed200,
                color300 = TeamPaletteColor.KtRed300,
                color500 = TeamPaletteColor.KtRed500,
                color600 = TeamPaletteColor.KtRed600
            )
            "ssg" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.SsgDeepred100,
                color200 = TeamPaletteColor.SsgDeepred200,
                color300 = TeamPaletteColor.SsgDeepred300,
                color500 = TeamPaletteColor.SsgDeepred500,
                color600 = TeamPaletteColor.SsgDeepred600
            )
            "ds" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.DsRed100,
                color200 = TeamPaletteColor.DsRed200,
                color300 = TeamPaletteColor.DsRed300,
                color500 = TeamPaletteColor.DsRed500,
                color600 = TeamPaletteColor.DsRed600
            )
            "kw" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.KwBurgundy100,
                color200 = TeamPaletteColor.KwBurgundy200,
                color300 = TeamPaletteColor.KwBurgundy300,
                color500 = TeamPaletteColor.KwBurgundy500,
                color600 = TeamPaletteColor.KwBurgundy600
            )
            "kia" -> TeamSecondaryPalette(
                color100 = TeamPaletteColor.KiaScarlet100,
                color200 = TeamPaletteColor.KiaScarlet200,
                color300 = TeamPaletteColor.KiaScarlet300,
                color500 = TeamPaletteColor.KiaScarlet500,
                color600 = TeamPaletteColor.KiaScarlet600
            )
            else -> error("Unknown team asset prefix: $assetPrefix")
        }
    }

    // 미디어 알림/잠금화면 앨범아트(team_cover_{prefix})처럼, 색상 외 다른 팀별 에셋도 이 prefix로
    // 찾을 수 있어서 모듈 내 다른 패키지(core.media 등)에서도 쓸 수 있게 internal로 엽니다.
    internal fun assetPrefixFor(teamId: TeamId): String {
        return when (teamId.value.trim().uppercase()) {
            "HANWHA" -> "hh"
            "LG" -> "lg"
            "LOTTE" -> "lt"
            "SAMSUNG" -> "ss"
            "NC" -> "nc"
            "KT" -> "kt"
            "SSG" -> "ssg"
            "DOOSAN" -> "ds"
            "KIWOOM" -> "kw"
            "KIA" -> "kia"
            else -> error("Unknown TeamId: ${teamId.value}")
        }
    }

    // 미니플레이어/알림·잠금화면 앨범아트가 같은 팀별 커버 썸네일(team_cover_thumb_{prefix})을
    // 각자 다른 방식(하드코딩 when / getIdentifier 동적 조회)으로 찾고 있었어서, assetPrefixFor
    // 기반의 매핑을 여기 한 곳으로 모읍니다. 팀이 추가/변경되면 이 함수만 고치면 됩니다.
    fun coverThumbnailRes(teamId: TeamId): Int? {
        return when (assetPrefixFor(teamId)) {
            "hh" -> R.drawable.team_cover_thumb_hh
            "lg" -> R.drawable.team_cover_thumb_lg
            "lt" -> R.drawable.team_cover_thumb_lt
            "ss" -> R.drawable.team_cover_thumb_ss
            "nc" -> R.drawable.team_cover_thumb_nc
            "kt" -> R.drawable.team_cover_thumb_kt
            "ssg" -> R.drawable.team_cover_thumb_ssg
            "ds" -> R.drawable.team_cover_thumb_ds
            "kw" -> R.drawable.team_cover_thumb_kw
            "kia" -> R.drawable.team_cover_thumb_kia
            else -> null
        }
    }
}
