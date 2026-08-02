package com.gms.cheerlotandroid.presentation.lineupplayback.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineupplayback.LineupPlaybackColors
import com.gms.cheerlotandroid.presentation.lineupplayback.toLineupPlaybackColors

@Composable
internal fun LineupPageControl(
    pageCount: Int,
    currentPage: Int,
    colors: LineupPlaybackColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val size by animateDpAsState(
                targetValue = if (isSelected) 10.dp else 8.dp,
                animationSpec = spring(),
                label = "pageIndicatorSize"
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    colors.selectedPageIndicatorColor
                } else {
                    colors.unselectedPageIndicatorColor
                },
                animationSpec = spring(),
                label = "pageIndicatorColor"
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LineupPageControlPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("SAMSUNG")) {
            LineupPageControl(
                pageCount = 9,
                currentPage = 2,
                colors = TeamTheme.colors.toLineupPlaybackColors(TeamId("SAMSUNG"))
            )
        }
    }
}
