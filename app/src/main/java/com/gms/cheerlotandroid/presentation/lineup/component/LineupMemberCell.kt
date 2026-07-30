package com.gms.cheerlotandroid.presentation.lineup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.LineupColors
import com.gms.cheerlotandroid.presentation.lineup.toLineupColors

@Composable
internal fun LineupMemberCell(
    player: PlayerInfo,
    colors: LineupColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasSong = player.cheerSongs.isNotEmpty()

    Row(
        modifier = modifier.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        player.battingOrder?.let { battingOrder ->
            Text(
                text = battingOrder.toString(),
                style = CheerLotTextStyle.M0,
                color = colors.battingOrderTextColor
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = player.name,
                style = CheerLotTextStyle.SB5LineupName,
                color = GrayScaleColor.GrayWhite,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible
            )
            Text(
                text = if (player.batThrow.isBlank()) {
                    player.position
                } else {
                    "${player.position}, ${player.batThrow}"
                },
                style = CheerLotTextStyle.M5Position,
                color = colors.positionTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible
            )
        }
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "재생",
            tint = if (hasSong) GrayScaleColor.GrayWhite else colors.playDisabledColor
        )
    }
}

@DevicePreviews
@Composable
private fun LineupMemberCellPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupMemberCell(
                player = PlayerInfo(
                    id = PlayerId("1"),
                    teamId = TeamId("LOTTE"),
                    name = "김민석",
                    backNumber = 51,
                    position = "외야수",
                    batThrow = "좌투좌타",
                    battingOrder = 1,
                    cheerSongs = emptyList()
                ),
                colors = TeamTheme.colors.toLineupColors(),
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TeamTheme.colors.primary)
                    .padding(16.dp)
            )
        }
    }
}
