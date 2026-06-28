package com.gms.cheerlotandroid.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.semantic.CheerLotColor
import com.gms.cheerlotandroid.design.typography.CheerLotTypography

// TODO: Android 화면 기준 ColorScheme 디자인팀과 확인 필요
private val LightColorScheme = lightColorScheme(
    primary = CheerLotColor.AppPrimary,
    secondary = CheerLotColor.AppSecondary,
    background = CheerLotColor.SystemBg,
    surface = CheerLotColor.SystemBg,
    onPrimary = GrayScaleColor.GrayWhite,
    onSecondary = GrayScaleColor.GrayWhite,
    onBackground = GrayScaleColor.GrayBlack,
    onSurface = GrayScaleColor.GrayBlack
)

@Composable
fun CheerLotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = CheerLotTypography,
        content = content
    )
}
