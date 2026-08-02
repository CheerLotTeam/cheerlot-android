package com.gms.cheerlotandroid.presentation.lineupchange

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.design.color.team.TeamColors

@Immutable
internal data class LineupChangeColors(
    val primaryColor: Color,
    val cellShadowColor: Color,
    val selectedCellStrokeColor: Color,
    val selectedCellFillColor: Color
)

internal fun TeamColors.toLineupChangeColors(): LineupChangeColors {
    return LineupChangeColors(
        primaryColor = primary,
        cellShadowColor = primary.copy(alpha = 0.15f),
        selectedCellStrokeColor = primaryPalette.color200,
        selectedCellFillColor = primaryPalette.color100
    )
}
