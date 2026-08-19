package com.gms.cheerlotandroid.presentation.lineup.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.LineupColors
import com.gms.cheerlotandroid.presentation.lineup.toLineupColors

@Composable
internal fun LineupEmptyState(
    gameStatus: GameStatus,
    colors: LineupColors,
    onToggleShowLineup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = when (gameStatus) {
        GameStatus.LINEUP_PENDING -> "오늘 라인업을 준비중이에요"
        GameStatus.OFF_DAY -> "오늘은 경기가 없는 날이에요"
        GameStatus.SEASON_ENDED -> "다음 시즌 준비중이에요"
        GameStatus.PLAYING_TODAY -> ""
    }
    val toggleShowLineupMessage = when (gameStatus) {
        GameStatus.LINEUP_PENDING,
        GameStatus.SEASON_ENDED -> "최근 경기 라인업 보기"
        GameStatus.OFF_DAY -> "이전 경기 라인업 보기"
        GameStatus.PLAYING_TODAY -> ""
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Text(
            text = message,
            style = CheerLotTextStyle.M3,
            color = colors.positionTextColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible
        )
        LineupToggleButton(
            text = toggleShowLineupMessage,
            colors = colors,
            onClick = onToggleShowLineup
        )
    }
}

@Composable
private fun LineupToggleButton(
    text: String,
    colors: LineupColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.primaryColor)
            .border(1.5.dp, colors.latestGameButtonStrokeColor, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(brush = colors.latestGameButtonGradient, alpha = 0.2f)
            drawRect(color = colors.positionTextColor, alpha = 0.2f)
        }
        Text(
            text = text,
            style = CheerLotTextStyle.SB8,
            color = GrayScaleColor.GrayWhite,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@DevicePreviews
@Composable
private fun LineupEmptyStatePreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupEmptyState(
                gameStatus = GameStatus.OFF_DAY,
                colors = TeamTheme.colors.toLineupColors(),
                onToggleShowLineup = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(TeamTheme.colors.primary)
            )
        }
    }
}
