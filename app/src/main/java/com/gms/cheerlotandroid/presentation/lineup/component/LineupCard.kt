package com.gms.cheerlotandroid.presentation.lineup.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.LineupColors
import com.gms.cheerlotandroid.presentation.lineup.LineupUiState
import com.gms.cheerlotandroid.presentation.lineup.toLineupColors

private val cardTopPadding = 20.dp
private val cardBottomPadding = 10.dp
private val cardContentSpacing = 4.dp
private val teamNameHeight = 44.5.dp
private val gameInfoHeight = 26.5.dp
private val separatorHeight = 1.dp
private const val LINEUP_PLAYER_COUNT = 9

@Composable
internal fun LineupCard(
    state: LineupUiState,
    colors: LineupColors,
    onPlayerClick: (PlayerInfo) -> Unit,
    onChangePlayer: (PlayerInfo) -> Unit,
    onToggleShowLineup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primaryColor)
            .border(2.dp, colors.cardStrokeColor, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = colors.cardBackgroundGradient, alpha = 0.2f)
        }
        Image(
            painter = painterResource(R.drawable.team_card_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.75f
                    blendMode = BlendMode.Softlight
                    compositingStrategy = CompositingStrategy.Offscreen
                },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = cardTopPadding, bottom = cardBottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(cardContentSpacing)
        ) {
            Text(
                text = state.teamEnglishName,
                style = CheerLotTextStyle.T1,
                color = GrayScaleColor.GrayWhite,
                maxLines = 1,
                modifier = Modifier.height(teamNameHeight)
            )
            LineupGameInfoBadge(
                gameInfoText = state.displayGameInfoText,
                starterPitcherName = state.displayStarterPitcherName,
                backgroundColor = colors.gameInfoBackgroundColor,
                modifier = Modifier.height(gameInfoHeight)
            )
            if (state.shouldShowLineup) {
                LineupPlayerList(
                    players = state.players,
                    colors = colors,
                    onPlayerClick = onPlayerClick,
                    onChangePlayer = onChangePlayer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        if (!state.shouldShowLineup) {
            LineupEmptyState(
                gameStatus = state.gameStatus,
                colors = colors,
                onToggleShowLineup = onToggleShowLineup,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LineupPlayerList(
    players: List<PlayerInfo>,
    colors: LineupColors,
    onPlayerClick: (PlayerInfo) -> Unit,
    onChangePlayer: (PlayerInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val lineupPlayers = players.take(LINEUP_PLAYER_COUNT)

    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        repeat(LINEUP_PLAYER_COUNT) { index ->
            val player = lineupPlayers.getOrNull(index)

            if (player != null) {
                LineupMemberCell(
                    player = player,
                    colors = colors,
                    onClick = { onPlayerClick(player) },
                    onChangePlayer = { onChangePlayer(player) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 5.5.dp)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            if (index < LINEUP_PLAYER_COUNT - 1) {
                LineupDashedDivider(color = colors.listDividerColor)
            } else {
                Spacer(modifier = Modifier.height(separatorHeight))
            }
        }
    }
}

@Composable
private fun LineupDashedDivider(color: Color, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(separatorHeight)
    ) {
        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = separatorHeight.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(3.dp.toPx(), 3.dp.toPx())
            )
        )
    }
}

@DevicePreviews
@Composable
private fun LineupCardPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupCard(
                state = LineupUiState(
                    teamId = TeamId("LOTTE"),
                    teamEnglishName = "LOTTE GIANTS",
                    teamShortName = "롯데",
                    opponentTeamName = "삼성",
                    players = List(LINEUP_PLAYER_COUNT) { index ->
                        PlayerInfo(
                            id = PlayerId("${index + 1}"),
                            teamId = TeamId("LOTTE"),
                            name = "선수 ${index + 1}",
                            backNumber = index + 1,
                            position = "내야수",
                            batThrow = "우투우타",
                            battingOrder = index + 1,
                            cheerSongs = emptyList()
                        )
                    },
                    showLineupOverride = true
                ),
                colors = TeamTheme.colors.toLineupColors(),
                onPlayerClick = {},
                onChangePlayer = {},
                onToggleShowLineup = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
    }
}
