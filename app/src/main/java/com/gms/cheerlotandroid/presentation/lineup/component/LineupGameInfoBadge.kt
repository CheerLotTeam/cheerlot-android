package com.gms.cheerlotandroid.presentation.lineup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId

@Composable
internal fun LineupGameInfoBadge(
    gameInfoText: String,
    starterPitcherName: String?,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 3.5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gameInfoText,
            style = CheerLotTextStyle.M5GameState,
            color = GrayScaleColor.GrayWhite,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible
        )
        if (starterPitcherName != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pitcher),
                    contentDescription = null,
                    tint = GrayScaleColor.GrayWhite,
                    modifier = Modifier.width(12.dp)
                )
                Text(
                    text = starterPitcherName,
                    style = CheerLotTextStyle.B4,
                    color = GrayScaleColor.GrayWhite,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LineupGameInfoBadgePreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupGameInfoBadge(
                gameInfoText = "7월 30일 | 롯데 vs 삼성",
                starterPitcherName = "박세웅",
                backgroundColor = TeamTheme.colors.primaryPalette.color500
            )
        }
    }
}
