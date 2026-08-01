package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.gms.cheerlotandroid.design.component.CustomTopAppBarGameInfo
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineupplayback.component.LineupPageControl
import com.gms.cheerlotandroid.presentation.lineupplayback.component.LineupPlayCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

private const val CARD_WIDTH_FRACTION = 300f / 360f
private const val CARD_ASPECT_RATIO = 300f / 476f
private const val SIDE_PAGE_SCALE = 0.91f
private const val SIDE_PAGE_ALPHA = 0.2f
private val CONTENT_SPACING = 24.dp
private val PAGE_CONTROL_HEIGHT = 10.dp

@Composable
internal fun LineupPlaybackScreen(
    state: LineupPlaybackUiState,
    teamId: TeamId,
    onClose: () -> Unit,
    onTogglePlayback: () -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val teamColors = TeamTheme.colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val colors = remember(teamColors, teamId) {
        teamColors.toLineupPlaybackColors(teamId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = backgroundColor)
            drawRect(brush = colors.playbackBackgroundGradient)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CustomTopAppBarGameInfo(
                    date = state.gameDate,
                    teams = state.teamsText,
                    onClose = onClose
                )
            }
        ) { innerPadding ->
            LineupPlaybackContent(
                state = state,
                colors = colors,
                onTogglePlayback = onTogglePlayback,
                onPageChanged = onPageChanged,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LineupPlaybackContent(
    state: LineupPlaybackUiState,
    colors: LineupPlaybackColors,
    onTogglePlayback: () -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.items.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = state.startIndex.coerceIn(state.items.indices),
        pageCount = state.items::size
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect(onPageChanged)
    }

    BoxWithConstraints(modifier = modifier) {
        val widthBasedCardWidth = maxWidth * CARD_WIDTH_FRACTION
        val availableCardHeight =
            (maxHeight - PAGE_CONTROL_HEIGHT - CONTENT_SPACING).coerceAtLeast(0.dp)
        val heightBasedCardWidth = availableCardHeight * CARD_ASPECT_RATIO
        val cardWidth = minOf(widthBasedCardWidth, heightBasedCardWidth)
        val cardHeight = cardWidth / CARD_ASPECT_RATIO
        val horizontalPadding = (maxWidth - cardWidth) / 2

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = CONTENT_SPACING,
                alignment = Alignment.CenterVertically
            )
        ) {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(cardWidth),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                modifier = Modifier.height(cardHeight)
            ) { page ->
                val pageOffset = pagerState.pageOffsetFor(page)

                LineupPlayCard(
                    item = state.items[page],
                    colors = colors,
                    isPlaying = state.isPlaying && pagerState.currentPage == page,
                    onTapPlayPause = onTogglePlayback,
                    modifier = Modifier
                        .width(cardWidth)
                        .aspectRatio(CARD_ASPECT_RATIO)
                        .graphicsLayer {
                            val scale = lerp(1f, SIDE_PAGE_SCALE, pageOffset)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(1f, SIDE_PAGE_ALPHA, pageOffset)
                        }
                )
            }
            LineupPageControl(
                pageCount = state.items.size,
                currentPage = pagerState.currentPage,
                colors = colors
            )
        }
    }
}

private fun PagerState.pageOffsetFor(page: Int): Float =
    ((currentPage - page) + currentPageOffsetFraction)
        .absoluteValue
        .coerceIn(0f, 1f)

@DevicePreviews
@Preview(showBackground = true, heightDp = 760)
@Composable
private fun LineupPlaybackScreenPreview() {
    val teamId = TeamId("SAMSUNG")
    CheerLotTheme {
        TeamTheme(teamId = teamId) {
            LineupPlaybackScreen(
                state = previewState,
                teamId = teamId,
                onClose = {},
                onTogglePlayback = {},
                onPageChanged = {}
            )
        }
    }
}

private val previewState = LineupPlaybackUiState(
    gameDate = "8월 1일",
    teamsText = "삼성 vs LG",
    items = List(9) { index ->
        LineupPlaybackItem(
            id = "${index + 1}-1",
            battingOrder = index + 1,
            memberName = "구자욱",
            cheerSongTitle = "기본 응원가",
            lyrics = "삼성의 구자욱 삼성의 구자욱\n안타를 날려버려 삼성 구자욱\n" +
                "삼성의 구자욱 삼성의 구자욱\n홈런을 날려버려 삼성 구자욱"
        )
    },
    startIndex = 0,
    isPlaying = false
)
