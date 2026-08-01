package com.gms.cheerlotandroid.design.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 텍스트 스타일 토큰입니다.
 *
 * 정의된 디자인 시스템에서는 lineHeight와 letterSpacing을 폰트 크기 대비 비율로 관리하고 있습니다.
 * Compose에서는 `sp` 값을 사용하므로 `size * ratio` 방식으로 변환합니다.
 */
object CheerLotTextStyle {
    // RobotoCondensed
    val T1 = robotoCondensed(size = 38, lineHeight = 1.0f, letterSpacing = 0f)
    val T2 = robotoCondensed(size = 30, lineHeight = 1.0f, letterSpacing = 0f)
    val T3 = robotoCondensed(size = 24, lineHeight = 0.9f, letterSpacing = 0f)

    // Pretendard Bold
    val B1 = pretendard(weight = FontWeight.Bold, size = 28, lineHeight = 1.3f, letterSpacing = -0.04f)
    val B1_1 = pretendard(weight = FontWeight.Bold, size = 28, lineHeight = 1.6f, letterSpacing = -0.04f)
    val B2 = pretendard(weight = FontWeight.Bold, size = 26, lineHeight = 1.3f, letterSpacing = -0.04f)
    val B3 = pretendard(weight = FontWeight.Bold, size = 24, lineHeight = 1.3f, letterSpacing = -0.04f)
    val B4 = pretendard(weight = FontWeight.Bold, size = 12, lineHeight = 1.2f, letterSpacing = 0f)

    // Pretendard SemiBold
    val SB1 = pretendard(weight = FontWeight.SemiBold, size = 48, lineHeight = 1.0f, letterSpacing = 0f)
    val SB2 = pretendard(weight = FontWeight.SemiBold, size = 32, lineHeight = 1.5f, letterSpacing = 0f)
    val SB3 = pretendard(weight = FontWeight.SemiBold, size = 24, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB4 = pretendard(weight = FontWeight.SemiBold, size = 22, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB5 = pretendard(weight = FontWeight.SemiBold, size = 20, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB5LineupName = pretendard(weight = FontWeight.SemiBold, size = 20, lineHeight = 1.0f, letterSpacing = 0f)
    val SB6 = pretendard(weight = FontWeight.SemiBold, size = 18, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB7 = pretendard(weight = FontWeight.SemiBold, size = 16, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB8 = pretendard(weight = FontWeight.SemiBold, size = 14, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB9 = pretendard(weight = FontWeight.SemiBold, size = 12, lineHeight = 1.3f, letterSpacing = -0.04f)
    val SB10 = pretendard(weight = FontWeight.SemiBold, size = 10, lineHeight = 1.2f, letterSpacing = 0f)

    // Pretendard Medium
    val M0 = pretendard(weight = FontWeight.Medium, size = 28, lineHeight = 1.3f, letterSpacing = 0f)
    val M1 = pretendard(weight = FontWeight.Medium, size = 20, lineHeight = 1.3f, letterSpacing = -0.04f)
    val M2 = pretendard(weight = FontWeight.Medium, size = 18, lineHeight = 1.3f, letterSpacing = -0.04f)
    val M3 = pretendard(weight = FontWeight.Medium, size = 16, lineHeight = 1.3f, letterSpacing = -0.04f)
    val M4 = pretendard(weight = FontWeight.Medium, size = 14, lineHeight = 1.3f, letterSpacing = -0.04f)
    val M5 = pretendard(weight = FontWeight.Medium, size = 12, lineHeight = 1.3f, letterSpacing = -0.04f)
    val M5GameState = pretendard(weight = FontWeight.Medium, size = 12, lineHeight = 1.2f, letterSpacing = 0f)
    val M5Position = pretendard(weight = FontWeight.Medium, size = 12, lineHeight = 1.0f, letterSpacing = -0.05f)
    val M6 = pretendard(weight = FontWeight.Medium, size = 10, lineHeight = 1.2f, letterSpacing = 0f)

    // Pretendard Regular
    val R1 = pretendard(weight = FontWeight.Normal, size = 16, lineHeight = 1.3f, letterSpacing = -0.04f)
    val R2 = pretendard(weight = FontWeight.Normal, size = 14, lineHeight = 1.3f, letterSpacing = -0.04f)
    val R3 = pretendard(weight = FontWeight.Normal, size = 12, lineHeight = 1.3f, letterSpacing = -0.04f)

    // TextStyle 생성 helper
    private fun robotoCondensed(
        size: Int,
        lineHeight: Float,
        letterSpacing: Float
    ): TextStyle {
        return textStyle(
            fontFamily = CheerLotFontFamily.RobotoCondensed,
            fontWeight = FontWeight.Black,
            size = size,
            lineHeight = lineHeight,
            letterSpacing = letterSpacing
        )
    }

    private fun pretendard(
        weight: FontWeight,
        size: Int,
        lineHeight: Float,
        letterSpacing: Float
    ): TextStyle {
        return textStyle(
            fontFamily = CheerLotFontFamily.Pretendard,
            fontWeight = weight,
            size = size,
            lineHeight = lineHeight,
            letterSpacing = letterSpacing
        )
    }

    private fun textStyle(
        fontFamily: FontFamily,
        fontWeight: FontWeight,
        size: Int,
        lineHeight: Float,
        letterSpacing: Float
    ): TextStyle {
        return TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = size.sp,
            lineHeight = (size * lineHeight).sp,
            letterSpacing = (size * letterSpacing).sp
        )
    }
}
