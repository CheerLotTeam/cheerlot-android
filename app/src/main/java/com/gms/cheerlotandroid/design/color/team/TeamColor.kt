package com.gms.cheerlotandroid.design.color.team

import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.design.color.semantic.CheerLotColor

/**
 * 팀 컬러 resolver입니다.
 *
 * `assetPrefix`는 팀 에셋 prefix 규칙을 따릅니다. 예: `ss`, `hh`, `lt`
 */
data class TeamColorSet(
    val primary: Color,
    val secondary: Color
)

/**
 * 팀 primary 팔레트입니다.
 *
 * `color400`은 팀 대표 primary 색상으로 별도 노출하기 때문에 팔레트에서는 제외합니다.
 */
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

    fun colorsFor(assetPrefix: String): TeamColorSet {
        return when (assetPrefix.lowercase()) {
            "hh" -> TeamColorSet(HanwhaPrimary, HanwhaSecondary)
            "lg" -> TeamColorSet(LgPrimary, LgSecondary)
            "lt" -> TeamColorSet(LottePrimary, LotteSecondary)
            "ss" -> TeamColorSet(SamsungPrimary, SamsungSecondary)
            "nc" -> TeamColorSet(NcPrimary, NcSecondary)
            "kt" -> TeamColorSet(KtPrimary, KtSecondary)
            "ssg" -> TeamColorSet(SsgPrimary, SsgSecondary)
            "ds" -> TeamColorSet(DoosanPrimary, DoosanSecondary)
            "kw" -> TeamColorSet(KiwoomPrimary, KiwoomSecondary)
            "kia" -> TeamColorSet(KiaPrimary, KiaSecondary)
            else -> TeamColorSet(CheerLotColor.AppPrimary, CheerLotColor.AppSecondary)
        }
    }

    // 팀별 primary 팔레트
    fun primaryPaletteFor(assetPrefix: String): TeamPrimaryPalette {
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
            else -> TeamPrimaryPalette(
                color100 = Color.Transparent,
                color200 = Color.Transparent,
                color300 = Color.Transparent,
                color500 = Color.Transparent,
                color600 = Color.Transparent
            )
        }
    }

    // 팀별 secondary 팔레트
    fun secondaryPaletteFor(assetPrefix: String): TeamSecondaryPalette {
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
            else -> TeamSecondaryPalette(
                color100 = Color.Transparent,
                color200 = Color.Transparent,
                color300 = Color.Transparent,
                color500 = Color.Transparent,
                color600 = Color.Transparent
            )
        }
    }
}
