package com.gms.cheerlotandroid.presentation.lineup

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.team.TeamColors

@Immutable
internal data class LineupColors(
    val primaryColor: Color,
    val cardBackgroundGradient: Brush,
    val latestGameButtonGradient: Brush,
    val cardStrokeColor: Color,
    val latestGameButtonStrokeColor: Color,
    val cardTextShadowColor: Color,
    val battingOrderTextColor: Color,
    val positionTextColor: Color,
    val gameInfoBackgroundColor: Color,
    val playDisabledColor: Color,
    val listDividerColor: Color
)

internal fun TeamColors.toLineupColors(): LineupColors {
    return LineupColors(
        primaryColor = primary,
        cardBackgroundGradient = Brush.verticalGradient(
            colors = listOf(
                primaryPalette.color600,
                primaryPalette.color200
            )
        ),
        latestGameButtonGradient = Brush.verticalGradient(
            colors = listOf(
                primaryPalette.color300,
                primaryPalette.color600
            )
        ),
        cardStrokeColor = primaryPalette.color200,
        latestGameButtonStrokeColor = primaryPalette.color300,
        cardTextShadowColor = primaryPalette.color600,
        battingOrderTextColor = if (primary == secondary) {
            GrayScaleColor.GrayWhite
        } else {
            secondary
        },
        positionTextColor = primaryPalette.color200,
        gameInfoBackgroundColor = primaryPalette.color500,
        playDisabledColor = primaryPalette.color300,
        listDividerColor = primaryPalette.color300
    )
}
