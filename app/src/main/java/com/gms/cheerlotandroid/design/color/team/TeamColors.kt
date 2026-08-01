package com.gms.cheerlotandroid.design.color.team

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// TeamColors: 해석 결과를 담는 불변 데이터 타입
@Immutable
data class TeamColors(
    val primary: Color,
    val secondary: Color,
    val primaryPalette: TeamPrimaryPalette,
    val secondaryPalette: TeamSecondaryPalette
)
