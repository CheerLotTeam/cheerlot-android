package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.team.TeamColors
import com.gms.cheerlotandroid.domain.model.team.TeamId

@Immutable
internal data class LineupPlaybackColors(
    val primaryColor: Color,
    val cardBackgroundGradient: Brush,
    val playbackBackgroundGradient: Brush,
    val cardStrokeColor: Color,
    val cardContentsColor: Color,
    val battingOrderTextColor: Color,
    val selectedPageIndicatorColor: Color,
    val unselectedPageIndicatorColor: Color
)

internal fun TeamColors.toLineupPlaybackColors(teamId: TeamId): LineupPlaybackColors {
    return LineupPlaybackColors(
        primaryColor = primary,
        cardBackgroundGradient = Brush.linearGradient(
            colorStops = arrayOf(
                0f to primaryPalette.color600,
                0.66f to primaryPalette.color100,
                1f to primaryPalette.color300
            )
        ),
        playbackBackgroundGradient = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.7f to Color.Transparent,
                1f to primaryPalette.color200
            )
        ),
        cardStrokeColor = primaryPalette.color200,
        cardContentsColor = primaryPalette.color200,
        battingOrderTextColor = if (primary == secondary) GrayScaleColor.GrayWhite else secondary,
        selectedPageIndicatorColor = primaryPalette.color300,
        unselectedPageIndicatorColor = if (
            teamId.value.equals("HANWHA", ignoreCase = true) ||
            teamId.value.equals("KT", ignoreCase = true)
        ) {
            primaryPalette.color200
        } else {
            GrayScaleColor.GrayWhite
        }
    )
}
