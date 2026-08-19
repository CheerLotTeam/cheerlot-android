package com.gms.cheerlotandroid.design.typography

import androidx.compose.material3.Typography

/**
 * 기본 Compose 컴포넌트에서 사용할 Material3 Typography 매핑입니다.
 *
 * 화면에서 세부 텍스트 토큰이 필요하면 `CheerLotTextStyle`을 직접 사용합니다.
 * TODO: Android 화면 기준 Typography 디자인팀과 확인 필요
 */
val CheerLotTypography = Typography(
    displayLarge = CheerLotTextStyle.T1,
    displayMedium = CheerLotTextStyle.T2,
    displaySmall = CheerLotTextStyle.T3,
    headlineLarge = CheerLotTextStyle.B1,
    headlineMedium = CheerLotTextStyle.B2,
    headlineSmall = CheerLotTextStyle.B3,
    titleLarge = CheerLotTextStyle.SB5,
    titleMedium = CheerLotTextStyle.SB7,
    titleSmall = CheerLotTextStyle.SB8,
    bodyLarge = CheerLotTextStyle.R1,
    bodyMedium = CheerLotTextStyle.R2,
    bodySmall = CheerLotTextStyle.R3,
    labelLarge = CheerLotTextStyle.SB8,
    labelMedium = CheerLotTextStyle.M5,
    labelSmall = CheerLotTextStyle.SB10
)
