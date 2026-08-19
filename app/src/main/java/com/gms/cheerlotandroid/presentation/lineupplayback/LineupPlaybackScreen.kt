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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.design.component.CustomTopAppBarGameInfo
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
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

// 실제 재생 시작(PlayLineupSongsUseCase)은 이 화면으로 넘어오기 전 LineupViewModel 또는
// CheerSongMenuSheet에서 이미 처리했다는 전제로, 여기서는 audioPlayer.state를 관찰합니다.
@Composable
internal fun LineupPlaybackScreen(
    startIndex: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LineupPlaybackViewModel = viewModel(
        factory = LocalAppContainer.current.viewModelFactory
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val teamId = state.teamId

    // 닫기 버튼, 시스템 back, Sheet dismiss 등 어떤 경로로 화면을 떠나든 재생을 완전히 끊습니다.
    // iOS LineupPlaybackView의 onClose/onDisappear에서 stop()을 호출하는 것과 같은 목적입니다.
    DisposableEffect(viewModel) {
        viewModel.trackPresented()
        onDispose(viewModel::stopPlayback)
    }

    if (teamId == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    TeamTheme(teamId = teamId) {
        LineupPlaybackContent(
            state = state,
            teamId = teamId,
            startIndex = startIndex,
            onClose = onClose,
            onTogglePlayback = viewModel::onTogglePlayback,
            onPageChanged = viewModel::onPageChanged,
            modifier = modifier
        )
    }
}

@Composable
private fun LineupPlaybackContent(
    state: LineupPlaybackUiState,
    teamId: TeamId,
    startIndex: Int,
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
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LineupPlaybackPager(
                    state = state,
                    colors = colors,
                    initialStartIndex = startIndex,
                    onTogglePlayback = onTogglePlayback,
                    onPageChanged = onPageChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LineupPlaybackPager(
    state: LineupPlaybackUiState,
    colors: LineupPlaybackColors,
    initialStartIndex: Int,
    onTogglePlayback: () -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.items.isEmpty()) return

    val itemCount = state.items.size
    val initialItemIndex = initialStartIndex.coerceIn(state.items.indices)

    // 앞·중앙·뒤에 동일한 목록을 배치하고 중앙 복제본에서 시작해 양방향 무한 스크롤처럼 보이게 합니다.
    val pagerPageCount = if (itemCount > 1) itemCount * 3 else itemCount
    val initialPage = if (itemCount > 1) itemCount + initialItemIndex else initialItemIndex
    val pagerState = rememberPagerState(initialPage = initialPage) { pagerPageCount }
    var lastSettledItemIndex by remember(itemCount) { mutableIntStateOf(initialItemIndex) }
    var pendingPlaybackItemIndex by remember(itemCount) { mutableStateOf<Int?>(null) }
    var hasObservedInitialPlaybackIndex by remember(initialStartIndex, itemCount) {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState, itemCount) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settledPage ->
                val itemIndex = settledPage % itemCount
                if (itemIndex != lastSettledItemIndex) {
                    lastSettledItemIndex = itemIndex
                    if (pendingPlaybackItemIndex == itemIndex) {
                        pendingPlaybackItemIndex = null
                    } else {
                        onPageChanged(itemIndex)
                    }
                }

                if (itemCount > 1) {
                    // 바깥 복제본에 도착하면 같은 선수를 가리키는 중앙 페이지로 애니메이션 없이 재배치합니다.
                    val centeredPage = calculateCenteredPagerPage(
                        settledPage = settledPage,
                        itemCount = itemCount
                    )
                    if (centeredPage != null) {
                        pagerState.scrollToPage(centeredPage)
                    }
                }
            }
    }

    // 재생 큐가 다음 곡으로 넘어가면 가장 가까운 복제 카드로 이동합니다.
    // Pager 콜백으로 시작된 이동과 구분해 이미 재생 중인 곡을 playAt으로 다시 시작하지 않습니다.
    LaunchedEffect(state.currentPlaybackIndex, itemCount) {
        // StateFlow가 보관한 이전 화면의 인덱스로 최초 카드 위치를 덮어쓰지 않습니다.
        // 최초 위치는 route의 startIndex로 정하고, 이후 큐 인덱스 변경부터 자동 이동합니다.
        if (!hasObservedInitialPlaybackIndex) {
            hasObservedInitialPlaybackIndex = true
            return@LaunchedEffect
        }

        val playbackItemIndex = state.currentPlaybackIndex.coerceIn(state.items.indices)
        if (pagerState.currentPage % itemCount == playbackItemIndex) return@LaunchedEffect

        // 현재 페이지에서 가장 가까운 복제본을 선택해 불필요하게 여러 카드를 통과하지 않도록 합니다.
        val targetPage = calculateNearestPagerPage(
            currentPage = pagerState.currentPage,
            itemIndex = playbackItemIndex,
            itemCount = itemCount
        )
        pendingPlaybackItemIndex = playbackItemIndex
        pagerState.animateScrollToPage(targetPage)
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

                // 복제된 Pager 페이지를 실제 라인업 선수 인덱스로 변환합니다.
                val itemIndex = page % itemCount

                LineupPlayCard(
                    item = state.items[itemIndex],
                    colors = colors,
                    isPlaying = state.isPlaying && itemIndex == state.currentPlaybackIndex,
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
                pageCount = itemCount,
                currentPage = pagerState.currentPage % itemCount,
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
private fun LineupPlaybackContentPreview() {
    val teamId = TeamId("SAMSUNG")
    CheerLotTheme {
        TeamTheme(teamId = teamId) {
            LineupPlaybackContent(
                state = previewState,
                teamId = teamId,
                startIndex = 3,
                onClose = {},
                onTogglePlayback = {},
                onPageChanged = {}
            )
        }
    }
}

private val previewState = LineupPlaybackUiState(
    teamId = TeamId("SAMSUNG"),
    teamShortName = "삼성",
    opponentTeamName = "LG",
    gameInfo = TeamGameInfo(
        teamId = TeamId("SAMSUNG"),
        status = GameStatus.PLAYING_TODAY,
        opponentTeamId = TeamId("LG"),
        starterPitcherName = "최원석",
        lastGameDate = "2026-08-01",
        isHome = false
    ),
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
    currentPlaybackIndex = 3,
    isPlaying = false,
    isLoading = false
)
